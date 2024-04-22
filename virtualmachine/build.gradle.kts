plugins {
    kotlin("jvm")
    application
}

group = "breadmod.rnd"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass = "breadmod.rnd.MainKt"
}

kotlin {
    jvmToolchain(17)
}
