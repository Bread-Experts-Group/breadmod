package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadePlugin

@LanguageDataGenerator
internal class EnglishUSLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
	private fun String.joinUnderscoreWithCaps() =
		this.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

	override fun assureName(name: String, otherwise: String) =
		if (name == "<null>") otherwise.substringAfterLast('.').joinUnderscoreWithCaps() else name

	override fun addManualTranslations() {
		this.bmAdd("config.jade.plugin_${JadePlugin.BLOCK_DATA.toLanguageKey()}", "Breadmod jade data provider")
	}

	override fun getNameAdditional(): String = "(with automatic naming)"
}