package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable

@LanguageDataGenerator
internal class EnglishUSLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
	private fun String.joinUnderscoreWithCaps(): String = this.split("_")
		.joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

	override fun assureName(name: String, otherwise: String) =
		if (name == "<null>") otherwise.substringAfterLast('.').joinUnderscoreWithCaps()
		else name

	override fun addManualTranslations() {
		this.bmAdd(modTranslatable("irc", "connected"), "Connected to host \"%1\$s:%2\$s\"")
		this.bmAdd(modTranslatable("irc", "unknown_host"), "Cannot connect, unknown host \"%1\$s:%2\$s\"")
		this.bmAdd(modTranslatable("irc", "timed_out"), "Cannot connect, timed out \"%1\$s:%2\$s\"")
		this.bmAdd(
			modTranslatable("irc", "connection_failed"),
			"Cannot connect to host \"%1\$s:%2\$s\": [%3\$s] \"%4\$s\""
		)
		this.bmAdd(
			modTranslatable("irc", "connection_failure"),
			"Host \"%1\$s:%2\$s\" connection failure: [%3\$s] \"%4\$s\""
		)
		this.bmAdd(modTranslatable("tool_gun", "settings", "title"), "Tool Gun Setup Utility V1.0")
		this.add("modmenu.nameTranslation.breadmod", "Bread Mod")
		this.add("jei.breadmod.generic.recipe_time", "%ss")
	}

	override fun getNameAdditional(): String = "(with automatic naming)"
}