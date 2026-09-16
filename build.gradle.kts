import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // see https://fabricmc.net/develop/ for new versions
    id("fabric-loom") version("1.17-SNAPSHOT") apply false
    // see https://projects.neoforged.net/neoforged/moddevgradle for new versions
    id("net.neoforged.moddev") version("2.0.146") apply false

    kotlin("jvm") version ("2.4.20") apply false
    java
    idea
}

@Suppress("AvoidApplyPluginMethod")
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.gradle.idea")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
    }

    repositories {
        mavenLocal()
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    tasks.withType<JavaCompile>().configureEach {
        if (name == "compileTestJava") {
            enabled = false
            return@configureEach
        }
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.withType<KotlinCompile>().configureEach {
        if (name == "compileTestKotlin") {
            enabled = false
            return@configureEach
        }
        compilerOptions {
            if (project.name != "common") source(project(":common").sourceSets.main.get().allSource)
            javaParameters = true
        }
    }

    tasks.withType<Test>().configureEach {
        enabled = false
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }

    idea.module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}