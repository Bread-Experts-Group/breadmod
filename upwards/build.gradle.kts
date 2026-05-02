plugins {
	kotlin("jvm") version "2.3.21"
	id("org.jetbrains.dokka-javadoc") version "2.1.0"
	id("idea")
	id("net.neoforged.moddev") version "2.0.141"
	`maven-publish`
	`java-library`
}

group = project.properties["mod_group_id"] as String
version = project.properties["mod_version"] as String

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

base.archivesName = project.properties["mod_id"] as String

repositories {
	mavenCentral()
	mavenLocal()
}

neoForge {
	version = project.properties["neo_version"] as String

	parchment {
		minecraftVersion = project.properties["minecraft_version"] as String
		mappingsVersion = "2024.11.17"
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = "upwards"
			from(components["java"])
			pom {
				name = "Upwards"
				description = "Upwards."
				url = "https://breadexperts.group"
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
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

tasks.jar {
	manifest {
		attributes(
			"FMLModType" to "LIBRARY",
			"Implementation-Version" to "0"
		)
	}
}