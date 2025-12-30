@file:Suppress("ImplicitThis", "UnstableApiUsage")

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

dependencies {
	testImplementation(kotlin("test"))
	implementation(kotlin("reflect"))
	implementation("org.bread_experts_group:bread_server_lib-code:D1F2N6P75")
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
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}