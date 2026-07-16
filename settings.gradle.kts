pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        versions("1.20.6", "1.21.1", "1.21.10", "1.21.11", "26.1.2", "26.2")
        vcsVersion = "26.2"
    }
}

rootProject.name = "Offline Mode Fix"
