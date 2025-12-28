@file:Suppress("ImplicitThis", "UnstableApiUsage")

pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
		maven {
			name = "NeoForge Releases"
//			url = uri("https://maven.neoforged.net/releases")
			// todo REPLACE WITH BEG MAVEN WHEN IT EXISTS
			url = uri("file:///home/logan/.m2/repository")
		}
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
