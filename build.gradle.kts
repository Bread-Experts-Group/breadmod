@file:Suppress("ImplicitThis", "UnstableApiUsage")

import net.neoforged.moddevgradle.dsl.RunModel
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.slf4j.event.Level
import java.util.Properties

plugins {
	kotlin("jvm") version "2.2.0"
	id("org.jetbrains.dokka-javadoc") version "2.0.0"
	id("idea")
	id("net.neoforged.moddev") version "2.0.80"
	`maven-publish`
	`java-library`
	signing
}

group = project.properties["mod_group_id"] as String
version = project.properties["mod_version"] as String

private fun getModId(): String = project.properties["mod_id"] as String
private fun RunModel.enableTestNamespaces(): Unit = systemProperty("neoforge.enabledGameTestNamespaces", getModId())
private fun mcVersion(): String = project.properties["minecraft_version"] as String
private val breadServerLib: String = "org.bread_experts_group:bread_server_lib-code:3.1.0"

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
		name = "JEI Maven"
		url = uri("https://maven.blamejared.com/")
	}
	maven {
		name = "JEI Backup Maven / ModMaven"
		url = uri("https://modmaven.dev")
	}
	maven {
		name = "CurseForge Maven"
		url = uri("https://www.cursemaven.com")
		content { includeGroup("curse.maven") }
	}
	maven {
		name = "Create Maven"
		url = uri("https://maven.createmod.net")
	}
	maven {
		name = "Registrate Maven"
		url = uri("https://mvn.devos.one/snapshots")
	}
	maven {
		name = "ForgeConfigAPIPort"
		url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	}
	maven {
		name = "Bread Experts Group Maven"
		url = uri("https://maven.breadexperts.group/")
	}

	exclusiveContent {
		forRepository {
			maven {
				name = "Bread Experts Group Maven"
				url = uri("https://maven.breadexperts.group/")
			}
		}
		filter {
			includeGroup("org.bread_experts_group")
		}
	}
}

neoForge {
	version = project.properties["neo_version"] as String

	parchment {
		minecraftVersion = mcVersion()
		mappingsVersion = "2024.11.17"
	}

	accessTransformers {
		file("src/main/resources/META-INF/accesstransformer.cfg")
	}

	runs {
		create("client") {
			client()
			enableTestNamespaces()
			devLogin = true
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
			logLevel = Level.INFO
			additionalRuntimeClasspathConfiguration.dependencies.add(
				dependencies.create(breadServerLib) {
					isTransitive = false
				}
			)
			additionalRuntimeClasspathConfiguration.dependencies.add(
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib:2.2.0-RC3") { isTransitive = false }
			)
			additionalRuntimeClasspathConfiguration.dependencies.add(
				dependencies.create("org.jetbrains.kotlin:kotlin-reflect:2.2.0-RC2") { isTransitive = false }
			)
			additionalRuntimeClasspathConfiguration.dependencies.add(
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0-RC3") { isTransitive = false }
			)
			additionalRuntimeClasspathConfiguration.dependencies.add(
				dependencies.create("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.0-RC3") { isTransitive = false }
			)
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
	jarJar(implementation(breadServerLib) {})
	jarJar(implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0-RC3") {})
	jarJar(implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0-RC2") {})
	jarJar(implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0-RC3") {})
	jarJar(implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.0-RC3") {})
	// Mod Compatibility //
	// Jade (WAILA)
	implementation("curse.maven:jade-324717:5976517")
//    runtimeOnly "curse.maven:the-one-probe-245211:5836106"
	runtimeOnly("curse.maven:packet-fixer-689467:6195911")
	implementation("curse.maven:projecte-226410:6611984")
	// Just Enough Items (JEI)
	val jeiVersion = "19.21.2.313"
	compileOnly("mezz.jei:jei-${mcVersion()}-neoforge-api:${jeiVersion}")
	runtimeOnly("mezz.jei:jei-${mcVersion()}-neoforge:${jeiVersion}")
	// Mekanism
	val mekanismVersion = "${mcVersion()}-10.7.9.72"
	compileOnly("mekanism:Mekanism:${mekanismVersion}:api")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:additions")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:generators")
	runtimeOnly("mekanism:Mekanism:${mekanismVersion}:tools")
	// Create
//	val createVersion = "6.0.0-4"
//	val ponderVersion = "1.0.39"
//	val flywheelVersion = "1.0.0-9"
//	val registrateVersion = "MC1.21-1.3.0+62"
//	implementation("com.simibubi.create:create-${mcVersion()}:$createVersion") { isTransitive = false }
//	implementation("net.createmod.ponder:Ponder-NeoForge-${mcVersion()}:$ponderVersion")
//	compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${mcVersion()}:$flywheelVersion")
//	runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${mcVersion()}:$flywheelVersion")
//	implementation("com.tterrag.registrate:Registrate:$registrateVersion")
	// WorldEdit
	runtimeOnly("curse.maven:worldedit-225608:5830452")
}
kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}
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
				url = "https://breadexperts.group"
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
						email = "miko@breadexperts.group"
					}
					developer {
						id = "loganm"
						name = "Logan McLean"
						email = "thetoaster@breadexperts.group"
					}
				}
				scm {
					connection = "scm:git:git://github.com/Bread-Experts-Group/breadmod.git"
					developerConnection = "scm:git:ssh://git@github.com:Bread-Experts-Group/breadmod.git"
					url = "https://breadexperts.group"
				}
			}
		}
	}
	repositories {
		maven {
			url = uri("https://maven.breadexperts.group/")
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
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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