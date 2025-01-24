package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component

@LanguageDataGenerator
internal class EnglishUSLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
	private fun String.joinUnderscoreWithCaps() =
		this.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

	override fun assureName(name: String, otherwise: String) =
		if (name == "<null>") otherwise.substringAfterLast('.').joinUnderscoreWithCaps() else name

	override fun addManualTranslations() {
		this.bmAdd(Component.translatable("modmenu.nameTranslation.breadmod"), "Bread Mod")
	}

	override fun getNameAdditional(): String = "(with automatic naming)"
}