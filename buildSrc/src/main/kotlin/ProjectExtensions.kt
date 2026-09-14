@file:Suppress("ImplicitThis", "KDocMissingDocumentation", "unused")

import org.gradle.api.Project
import java.io.File

fun Project.getProp(property: String): String = providers.gradleProperty(property).get()

val Project.forgeATLocation: java.io.File
	get() = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")

val Project.kotlin_Version: String
	get() = getProp("kotlin_version")
val Project.minecraft_Version: String
	get() = getProp("minecraft_version")
val Project.neoForm_Version: String
	get() = getProp("neo_form_version")
val Project.parchment_Version: String
	get() = getProp("parchment_version")
val Project.neoforge_Version: String
	get() = getProp("neoforge_version")
val Project.forge_Version: String
	get() = getProp("forge_version")
val Project.fabric_loader_Version: String
	get() = getProp("fabric_loader_version")
val Project.fabric_Version: String
	get() = getProp("fabric_version")
val Project.mod_Name: String
	get() = getProp("mod_name")
val Project.mod_Id: String
	get() = getProp("mod_id")

val Project.breadlib_Version: String
	get() = getProp("breadlib_version")