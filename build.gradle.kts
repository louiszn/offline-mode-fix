import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val minecraftVersion = sc.current.version
val isObfuscated = sc.current.parsed < "26.1"

val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modVersion = property("mod.version").toString()
val minecraftDependency = property("mod.mc_dep").toString()
val minecraftTitle = property("mod.mc_title").toString()
val minecraftTargets = property("mod.mc_targets").toString().split(' ')

val fabricApiVersion = property("deps.fabric_api").toString()
val fabricLoaderVersion = property("deps.fabric_loader").toString()

val clothConfigVersion = property("deps.cloth_config_version").toString()
val modMenuVersion = property("deps.modmenu_version").toString()

val modrinthProjectId = property("publish.modrinth").toString()
val releaseVersion = "$modVersion+$minecraftVersion"

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val loomPlugin = if (isObfuscated) {
    "net.fabricmc.fabric-loom-remap"
} else {
    "net.fabricmc.fabric-loom"
}
pluginManager.apply(loomPlugin)

version = releaseVersion
base.archivesName = modId

repositories {
    fun strictMaven(
        url: String,
        alias: String,
        vararg groups: String,
    ) = exclusiveContent {
        forRepository {
            maven(url) {
                name = alias
            }
        }

        filter {
            groups.forEach(::includeGroup)
        }
    }

    strictMaven(
        "https://www.cursemaven.com",
        "CurseForge",
        "curse.maven",
    )

    strictMaven(
        "https://api.modrinth.com/maven",
        "Modrinth",
        "maven.modrinth",
    )

    strictMaven(
        "https://maven.shedaniel.me/",
        "Shedaniel",
        "me.shedaniel.cloth",
    )

    strictMaven(
        "https://maven.terraformersmc.com/releases/",
        "Terraformers",
        "com.terraformersmc",
    )
}

dependencies {
    add(
        "minecraft",
        "com.mojang:minecraft:$minecraftVersion",
    )

    if (isObfuscated) {
        val loom = project.extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>()

        add(
            "mappings",
            loom.officialMojangMappings(),
        )
    }

    fun addDependency(
        dependency: String,
        optional: Boolean = false,
        includeRuntime: Boolean = false,
        excludeGroups: List<String> = emptyList(),
    ) {
        val configuration = when {
            isObfuscated && optional -> "modCompileOnly"
            isObfuscated -> "modImplementation"
            optional -> "compileOnly"
            else -> "implementation"
        }

        fun addDep(configuration: String) {
            add(configuration, dependency) {
                excludeGroups.forEach { exclude(group = it) }
            }
        }

        addDep(configuration)

        if (includeRuntime) {
            addDep(if (isObfuscated) "modRuntimeOnly" else "runtimeOnly")
        }
    }

    addDependency("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    addDependency("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    addDependency(
        "me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion",
        excludeGroups = listOf("net.fabricmc.fabric-api"),
    )

    addDependency(
        "com.terraformersmc:modmenu:$modMenuVersion",
        optional = true,
        includeRuntime = true,
    )
}

configure<net.fabricmc.loom.api.LoomGradleExtensionAPI> {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put(
            "mark-corresponding-synthetics",
            "1",
        )
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

java {
    withSourcesJar()

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
}

tasks {
    processResources {
        val properties = mapOf(
            "id" to modId,
            "name" to modName,
            "version" to modVersion,
            "minecraft" to minecraftDependency,
        )

        inputs.properties(properties)

        filesMatching("fabric.mod.json") {
            expand(properties)
        }

        filesMatching("*.mixins.json") {
            expand(
                "java" to "JAVA_${requiredJava.majorVersion}",
            )
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"

        if (isObfuscated) {
            from(
                named("remapJar"),
                named("remapSourcesJar"),
            )
        } else {
            from(
                named("jar"),
                named("sourcesJar"),
            )
        }

        into(
            rootProject.layout.buildDirectory.dir(
                "libs/$modVersion",
            ),
        )

        dependsOn("build")
    }
}

publishMods {
    if (isObfuscated) {
        file.set(
            tasks.named<AbstractArchiveTask>("remapJar").flatMap { it.archiveFile },
        )

        additionalFiles.from(
            tasks.named<AbstractArchiveTask>("remapSourcesJar").flatMap { it.archiveFile },
        )
    } else {
        file.set(
            tasks.named<AbstractArchiveTask>("jar").flatMap { it.archiveFile },
        )

        additionalFiles.from(
            tasks.named<AbstractArchiveTask>("sourcesJar").flatMap { it.archiveFile },
        )
    }

    displayName = "$modName $modVersion for $minecraftTitle"
    version = releaseVersion
    changelog = System.getenv("CHANGELOG") ?: "See the release notes for changelog."
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        projectId = modrinthProjectId
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(minecraftTargets)

        requires {
            slug = "fabric-api"
        }
    }
}

fun modrinthVersionExists(
    projectId: String,
    versionNumber: String,
): Boolean {
    val encodedVersion = URLEncoder.encode(
        versionNumber,
        StandardCharsets.UTF_8,
    )

    val connection = URI(
        "https://api.modrinth.com/v2/project/$projectId/version/$encodedVersion",
    ).toURL().openConnection() as HttpURLConnection

    connection.apply {
        requestMethod = "GET"
        connectTimeout = 10_000
        readTimeout = 10_000

        setRequestProperty(
            "User-Agent",
            "louiszn/offline-mode-fix",
        )
    }

    return try {
        when (val status = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> true
            HttpURLConnection.HTTP_NOT_FOUND -> false

            else -> throw GradleException("Failed to check Modrinth version $versionNumber: HTTP $status")
        }
    } finally {
        connection.disconnect()
    }
}

tasks.named("publishModrinth") {
    onlyIf {
        val exists = modrinthVersionExists(
            modrinthProjectId,
            releaseVersion,
        )

        if (exists) {
            logger.lifecycle("Skipping Modrinth publish: $releaseVersion already exists.")
        } else {
            logger.lifecycle("Publishing Modrinth version: $releaseVersion")
        }

        !exists
    }
}
