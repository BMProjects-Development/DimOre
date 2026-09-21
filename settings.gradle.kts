import java.util.Properties

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }

    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("gg.meza.stonecraft") version "1.13.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) version("$version-$it", version)
        }

        rootProject.projectDir.resolve("versions/dependencies")
            .listFiles()
            .filter { !it.isDirectory }
            .filter {
                val props = Properties()
                props.load(it.absoluteFile.inputStream())
                !props.containsKey("build.ignore") || props.getProperty("build.ignore", "false") != "true"
            }.forEach {
                val props = Properties()
                props.load(it.absoluteFile.inputStream())

                val fileName = it.name

                if (!props.containsKey("mod.platforms")) {
                    throw NullPointerException("mod.platforms is required for compile! Problem file: $fileName")
                }

                val platforms = props.getProperty("mod.platforms").split(',')
                val platformVersion = fileName.substringBeforeLast(".")

                mc(platformVersion, *platforms.toTypedArray())
            }
    }

    create(rootProject)
}

val archivesName: String by settings
rootProject.name = archivesName
