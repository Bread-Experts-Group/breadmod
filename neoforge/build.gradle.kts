@file:Suppress("ImplicitThis", "IncorrectFormatting", "KDocMissingDocumentation", "DEPRECATION")

plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

neoForge {
    version = neoforge_Version
    accessTransformers.from(forgeATLocation)
    parchment {
        minecraftVersion = minecraft_Version
        mappingsVersion = parchment_Version
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", mod_Id)
            ideName = "NeoForge ${this.name.capitalize()} (${project.path})" // Unify the run config names with fabric
        }
        create("client") {
            client()
            gameDirectory.set(File("$rootDir/runs/client"))
        }
        create("data") {
            data()
            programArguments.addAll(
                "--all",
                "--mod", getProp("mod_id"),
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
        create("server") {
            server()
            gameDirectory.set(File("$rootDir/runs/server"))
        }
    }
    mods {
        create(getProp("mod_id")) {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources {
    srcDirs("src/generated/resources")
}

dependencies {
    implementation("org.bread_experts_group:breadlib-neoforge-$minecraft_Version:$breadlib_Version")
}