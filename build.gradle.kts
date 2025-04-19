@file:Suppress("ImplicitThis")

import net.neoforged.moddevgradle.dsl.RunModel
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import java.util.Properties

plugins {
	kotlin("jvm") version "2.1.10"
	id("org.jetbrains.dokka-javadoc") version "2.0.0"
	id("idea")
	id("net.neoforged.moddev") version "2.0.42-beta"
	`maven-publish`
	`java-library`
	signing
}

group = project.properties["mod_group_id"] as String
version = project.properties["mod_version"] as String

private fun getModId() = project.properties["mod_id"] as String
private fun RunModel.enableTestNamespaces() = systemProperty("neoforge.enabledGameTestNamespaces", getModId())

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

base {
	archivesName = getModId()
}

repositories {
	mavenCentral()
	mavenLocal()
	maven {
		name = "Kotlin for Forge"
		url = uri("https://thedarkcolour.github.io/KotlinForForge/")
		content { includeGroup("thedarkcolour") }
	}
	maven {
		// location of the maven that hosts JEI files since January 2023
		name = "Jared's maven"
		url = uri("https://maven.blamejared.com/")
	}
	maven {
		// location of a maven mirror for JEI files, as a fallback
		name = "ModMaven"
		url = uri("https://modmaven.dev")
	}
	maven {
		url = uri("https://www.cursemaven.com")
		content { includeGroup("curse.maven") }
	}
	maven { url = uri("https://maven.createmod.net") } // Create, Ponder, Flywheel
	maven { url = uri("https://mvn.devos.one/snapshots") } // Registrate
	maven { url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") } // ForgeConfigAPIPort
}

neoForge {
	version = project.properties["neo_version"] as String

	parchment {
		minecraftVersion = "1.21.1"
		mappingsVersion = "2024.11.17"
	}

	accessTransformers {
		file("src/main/resources/META-INF/accesstransformer.cfg")
	}

	runs {
		create("client") {
			client()
			enableTestNamespaces()
		}
		create("client_PizzaTime65") {
			client()
			enableTestNamespaces()
			programArguments.addAll(
				"--username", "PizzaTime65",
				"--uuid", "30cdf636-82ed-47ee-9a9c-4d820c0d76a9"
			)
		}
		create("client_Meownium") {
			client()
			enableTestNamespaces()
			programArguments.addAll(
				"--username", "Meownium",
				"--uuid", "34e8274a-c02c-4c32-9311-2bccc9f6ba7d"
			)
		}
		create("server") {
			server()
			programArgument("--nogui")
			enableTestNamespaces()
		}
		create("gameTestServer") {
			type = "gameTestServer"
			enableTestNamespaces()
		}
		create("data") {
			data()
			programArguments.addAll(
				"--mod", getModId(),
				"--all",
				"--output", file("src/generated/resources/").absolutePath,
				"--existing", file("src/main/resources/").absolutePath
			)
		}
		configureEach {
			// Logging data for a UserDev environment
			// "SCAN": For mods scan.
			// "REGISTRIES": For firing of registry events.
			// "REGISTRYDUMP": For getting the contents of all registries.
			// systemProperty 'forge.logging.markers', 'REGISTRIES'
			logLevel = org.slf4j.event.Level.INFO
		}
	}

	mods {
		create(getModId()) {
			sourceSet(sourceSets.main.get())
		}
	}
}

dependencies {
	// Mod Dependencies //
	// KFF
	implementation("thedarkcolour:kotlinforforge-neoforge:5.7.0")
	// Mod Compatibility //
	// Jade (WAILA)
	implementation("curse.maven:jade-324717:5976517")
//    runtimeOnly "curse.maven:the-one-probe-245211:5836106"
	runtimeOnly("curse.maven:packet-fixer-689467:6195911")
	// Just Enough Items (JEI)
	val jeiVersion = "19.10.0.126"
	compileOnly("mezz.jei:jei-1.21.1-neoforge-api:${jeiVersion}")
	runtimeOnly("mezz.jei:jei-1.21.1-neoforge:${jeiVersion}")
	// Mekanism
	val mekanismVersion = "1.21.1-10.7.8.70"
	compileOnly("mekanism:Mekanism:${mekanismVersion}:api")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:additions")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:generators")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:tools")
	// Create
//    implementation("com.simibubi.create:create-${minecraft_version}:${create_version}") { transitive = false }
//    implementation("net.createmod.ponder:Ponder-NeoForge-${minecraft_version}:${ponder_version}")
//    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${minecraft_version}:${flywheel_version}")
//    runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${minecraft_version}:${flywheel_version}")
//    implementation("com.tterrag.registrate:Registrate:${registrate_version}")
	// WorldEdit
	runtimeOnly("curse.maven:worldedit-225608:5830452")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
tasks.register<Jar>("dokkaJavadocJar") {
	dependsOn(tasks.dokkaGeneratePublicationJavadoc)
	from(tasks.dokkaGeneratePublicationJavadoc.flatMap(DokkaGeneratePublicationTask::outputDirectory))
	archiveClassifier.set("javadoc")
}
private val localProperties: Properties = Properties().apply {
	rootProject.file("local.properties").reader().use(::load)
}
publishing {
	publications {
		create<MavenPublication>("mavenKotlin") {
			artifactId = "breadmod"
			from(components["kotlin"])
			artifact(tasks.kotlinSourcesJar)
			artifact(tasks["dokkaJavadocJar"])
			pom {
				name = "Bread Mod"
				description = "The Bread Mod."
				url = "https://javart.zip"
				signing {
					sign(publishing.publications["mavenKotlin"])
					sign(configurations.archives.get())
				}
				licenses {
					license {
						name = "GNU General Public License v3.0"
						url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
					}
				}
				developers {
					developer {
						id = "mikoe"
						name = "Miko Elbrecht"
						email = "miko@javart.zip"
					}
					developer {
						id = "loganm"
						name = "Logan McLean"
						email = "thetoaster@javart.zip"
					}
				}
				scm {
					connection = "scm:git:git://github.com/Bread-Experts-Group/breadmod.git"
					developerConnection = "scm:git:ssh://git@github.com:Bread-Experts-Group/breadmod.git"
					url = "https://javart.zip"
				}
			}
		}
	}
	repositories {
		maven {
			url = uri("https://maven.javart.zip/")
			credentials {
				username = localProperties["mavenUser"] as String
				password = localProperties["mavenPassword"] as String
			}
		}
	}
}
signing {
	useGpgCmd()
	sign(publishing.publications["mavenKotlin"])
}
tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
}

tasks.processResources {
	duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.register<ProcessResources>("generateModMetadata")
tasks.named<ProcessResources>("generateModMetadata") {
	val replaceProperties = mapOf(
		"minecraft_version" to "${project.properties["minecraft_version"]}",
		"minecraft_version_range" to "${project.properties["minecraft_version_range"]}",
		"neo_version" to "${project.properties["neo_version"]}",
		"neo_version_range" to "${project.properties["neo_version_range"]}",
		"loader_version_range" to "${project.properties["loader_version_range"]}",
		"mod_id" to "${project.properties["mod_id"]}",
		"mod_name" to "${project.properties["mod_name"]}",
		"mod_license" to "${project.properties["mod_license"]}",
		"mod_version" to "${project.properties["mod_version"]}",
		"mod_authors" to "${project.properties["mod_authors"]}",
		"mod_description" to "${project.properties["mod_description"]}",
	)
	inputs.properties(replaceProperties)
	expand(replaceProperties)
	from("src/main/templates/")
	into("build/generated/sources/modMetadata")
	duplicatesStrategy = DuplicatesStrategy.WARN
}

sourceSets.main.get().resources {
	srcDirs("src/generated/resources", tasks["generateModMetadata"])
}

neoForge.ideSyncTask(tasks["generateModMetadata"])