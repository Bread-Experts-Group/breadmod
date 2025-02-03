package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable

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
		this.bmAdd(modTranslatable("tool_gun", "settings", "title"), "Tool Gun Setup Utility V1.0")
	}

	override fun getNameAdditional(): String = "(with automatic naming)"
}