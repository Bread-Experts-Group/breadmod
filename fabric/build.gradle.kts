@file:Suppress("ImplicitThis", "UnstableApiUsage", "KDocMissingDocumentation")

plugins {
    id("multiloader-loader")
    id("fabric-loom")
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

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_Version")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraft_Version:$parchment_Version@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_Version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_Version")

    modImplementation("org.bread_experts_group:breadlib-fabric-$minecraft_Version:$breadlib_Version")

//    modImplementation("teamreborn:energy:4.1.0")
//    // https://modrinth.com/mod/techreborn/version/5.11.10
//    modImplementation("maven.modrinth:3eMENr4V:tYneg20h")
//    // https://modrinth.com/mod/reborncore/version/5.11.10 (dependency of tech reborn)
//    modImplementation("maven.modrinth:3NCrJdj3:9YLLekgJ")
}

loom {
    val aw = project(":common").file("src/main/resources/$mod_Id.accesswidener")
    if (aw.exists()) accessWidenerPath.set(aw)
    mixin {
        defaultRefmapName.set("$mod_Id.refmap.json")
        useLegacyMixinAp = true
    }
    runs {
        getByName("client") {
            client()
            displayName = "Fabric Client"
            generateRunConfig = true
            runDirectory.set(File("$rootDir/runs/client"))
        }
        getByName("server") {
            server()
            displayName = "Fabric Server"
            generateRunConfig = true
            runDirectory.set(File("$rootDir/runs/server"))
        }
    }
}
fabricApi {
    configureDataGeneration()
}