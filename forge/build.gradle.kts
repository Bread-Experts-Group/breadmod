@file:Suppress("ImplicitThis", "LongLine", "SpellCheckingInspection")

plugins {
    id("multiloader-loader")
    id("net.minecraftforge.gradle") version "7.0.31"
    id("net.minecraftforge.jarjar") version "0.2.3"
}

base.archivesName = "$mod_Name-forge-$minecraft_Version}"
jarJar.register {
    archiveClassifier = null
}
tasks.named("jar", Jar::class.java) {
    manifest {
        // Add our config to the manifest of our jar to have Mixin detect us at runtime.
        attributes["MixinConfigs"] = "[$mod_Id.forge.mixins.json, $mod_Id.mixins.json]"
    }
}

minecraft {
    mappings("official", minecraft_Version)

//    copyIdeResources = true //Calls processResources when in dev

//    reobf = false // Forge 1.20.6+ uses official mappings at runtime, so we shouldn't reobf from official to SRG

    // Automatically enable forge AccessTransformers if the file exists
    // This location is hardcoded in Forge and can not be changed.
    // https://github.com/MinecraftForge/MinecraftForge/blob/be1698bb1554f9c8fa2f58e32b9ab70bc4385e60/fmlloader/src/main/java/net/minecraftforge/fml/loading/moddiscovery/ModFile.java#L123
    // Forge still uses SRG names during compile time, so we cannot use the common AT's
    accessTransformers.from(forgeATLocation)

    runs {
        configureEach {
            args("forge.enabledGameTestNamespaces", mod_Id)
            args(
                "--mixin.config=$mod_Id.forge.mixins.json",
                "--mixin.config=$mod_Id.mixins.json"
            )
        }

        register("client") {
            workingDir = file("$rootDir/runs/client")
        }

        register("server") {
            args.add("--nogui")
            workingDir = file("$rootDir/runs/server")
        }

        register("gameTestServer")

        register("data") {
            workingDir = file(layout.projectDirectory.dir("run-data"))

            args.addAll(
                "--all",
                "--mod", mod_Id,
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
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
    implementation(minecraft.dependency("net.minecraftforge:forge:$minecraft_Version-$forge_Version"))
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4") {})
    annotationProcessor("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")

    // Forge's hack fix
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }

    implementation("org.bread_experts_group:breadlib-forge-$minecraft_Version:$breadlib_Version")
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(tasks.withType<ProcessResources>())
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourceSets/${it.name}")
    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
    it.kotlin.destinationDirectory = dir
}