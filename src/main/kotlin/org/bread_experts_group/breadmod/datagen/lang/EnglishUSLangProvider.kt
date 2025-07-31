package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable

@LanguageDataGenerator
internal class EnglishUSLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
	private fun String.joinUnderscoreWithCaps(): String = this.split("_")
		.joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

	override fun assureName(name: String, languageID: String) =
		if (name == "<null>") languageID.substringAfterLast('.').joinUnderscoreWithCaps()
		else name

	override fun addManualTranslations() {
		this.bmAdd(modTranslatable("irc", "connected"), "Connected to host [%1\$s]")
		this.bmAdd(modTranslatable("irc", "disconnected"), "Host disconnected [%1\$s]")
		this.bmAdd(modTranslatable("irc", "unknown_host"), "Cannot connect, unknown host \"%1\$s:%2\$s\"")
		this.bmAdd(modTranslatable("irc", "timed_out"), "Cannot connect, timed out \"[%1\$s]:%2\$s\"")
		this.bmAdd(
			modTranslatable("irc", "connection_failed"),
			"Cannot connect to host \"[%1\$s]:%2\$s\": [%3\$s] \"%4\$s\""
		)
		this.bmAdd(
			modTranslatable("irc", "connection_failure"),
			"Host [%1\$s] connection failure: [%2\$s] \"%3\$s\""
		)
		this.bmAdd(modTranslatable("tool_gun", "settings", "title"), "Tool Gun Setup Utility V1.0")
		this.add("modmenu.nameTranslation.breadmod", "Bread Mod")
		this.add("jei.breadmod.generic.recipe_time", "%ss")
		this.add("jei.breadmod.generic.recipe_energy", "Energy Needed: %sFE")
		this.add("item.breadmod.bread_armor.range", "range: %s %s")
		this.add("item.breadmod.bread_armor.tooltip", "Applied Effect:")

		this.add("command.breadmod.war_timer.toggle.success", "Toggled war timer to %s for %s")
	}

	override fun getNameAdditional(): String = "English (with automatic naming)"
}