plugins {
    // `maven-publish`
     id("me.modmuss50.mod-publish-plugin")
}

val isObfuscated = sc.current.parsed < "26.1"

if (isObfuscated) {
    apply(plugin = "net.fabricmc.fabric-loom-remap")
} else {
    apply(plugin = "net.fabricmc.fabric-loom")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    // `minecraft` is available in both plugins
    add("minecraft", "com.mojang:minecraft:${sc.current.version}")

    val fapiDep = "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}"
    val loaderDep = "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}"

    if (isObfuscated) {
        // Fetch the Loom API dynamically to grab official mappings
        val loomExt = project.extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>()
        add("mappings", loomExt.officialMojangMappings())

        add("modImplementation", loaderDep)
        add("modImplementation", fapiDep) // Depend on the whole API here
    } else {
        add("implementation", loaderDep)
        add("implementation", fapiDep)    // Depend on the whole API here
    }
}

configure<net.fabricmc.loom.api.LoomGradleExtensionAPI> {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks {
    processResources {
        inputs.property("id", project.property("mod.id"))
        inputs.property("name", project.property("mod.name"))
        inputs.property("version", project.property("mod.version"))
        inputs.property("minecraft", project.property("mod.mc_dep"))

        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to project.property("mod.version"),
            "minecraft" to project.property("mod.mc_dep")
        )

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        // Dynamically switch task dependencies to avoid 'remapJar' missing errors
        if (isObfuscated) {
            from(named("remapJar"), named("remapSourcesJar"))
        } else {
            from(named("jar"), named("sourcesJar"))
        }
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

// Publishes builds to Modrinth automatically
publishMods {
    if (isObfuscated) {
        file.set(tasks.named<AbstractArchiveTask>("remapJar").flatMap { it.archiveFile })
        additionalFiles.from(tasks.named<AbstractArchiveTask>("remapSourcesJar").flatMap { it.archiveFile })
    } else {
        file.set(tasks.named<AbstractArchiveTask>("jar").flatMap { it.archiveFile })
        additionalFiles.from(tasks.named<AbstractArchiveTask>("sourcesJar").flatMap { it.archiveFile })
    }

    displayName = "${property("mod.name")} ${property("mod.version")} for ${property("mod.mc_title")}"
    version = property("mod.version") as String

    // Grabs the commit message from GitHub Actions, or defaults to a fallback string
    changelog = System.getenv("CHANGELOG") ?: "See GitHub releases for changelog."
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))
        requires {
            slug = "fabric-api"
        }
    }
}
