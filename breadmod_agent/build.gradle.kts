@file:Suppress("ImplicitThis", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
	kotlin("jvm") version "2.3.0"
	id("net.neoforged.moddev") version "2.0.134"
}

group = "org.bread_experts_group"
version = "1.5.1-agent"

kotlin {
	jvmToolchain(25)
}

repositories {
	mavenCentral()
	mavenLocal()
}

neoForge {
	version = project.properties["neo_version"] as String

	parchment {
		minecraftVersion = "1.21.1"
		mappingsVersion = "2024.11.17"
	}
}
lateinit var cont: NamedDomainObjectProvider<Configuration>
configurations {
	cont = configurations.register("extraLibs")
}

dependencies {
	testImplementation(kotlin("test"))
	implementation("org.bread_experts_group:bread_server_lib-code:D1F2N6P75")
	compileOnly(rootProject)
	cont.get()("org.jetbrains.kotlin:kotlin-stdlib:2.3.0")
	cont.get()(kotlin("reflect"))
	cont.get()("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.0")
	cont.get()("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.3.0")
	configurations.compileClasspath.extendsFrom(cont)
}

tasks.jar {
	manifest {
		attributes(
			"Premain-Class" to "org.bread_experts_group.breadmod_agent.Agent"
		)
	}
//	from({
//		configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//	})
	from({
		cont.get().map { if (it.isDirectory) it else zipTree(it) }
	})
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}