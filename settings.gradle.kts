@file:Suppress("ImplicitThis", "UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    name = "Fabric"
                    url = uri("https://maven.fabricmc.net")
                }
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven {
            name = "Sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public")
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "Forge"
                    url = uri("https://maven.minecraftforge.net")
                }
            }
            filter {
                includeGroupAndSubgroups("net.minecraftforge")
            }
        }
    }

    plugins {
        val kotlinVersion: String = providers.gradleProperty("kotlin_version").get()
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        kotlin("kapt") version "2.4.10"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "breadlib_mod_template"
include("common")
include("fabric")
include("neoforge")
include("forge")
