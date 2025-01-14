package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput

@LanguageDataGenerator
internal class EnglishUSLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
	private fun String.joinUnderscoreWithCaps() =
		this.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

	override fun assureName(name: String, otherwise: String) =
		if (name == "<null>") otherwise.substringAfterLast('.').joinUnderscoreWithCaps() else name

	override fun addManualTranslations() {
		// CHRIS ADD THINGS HERE NOOOOOOOWWWWWWWWWWWWWWWWWW
		throw InternalError("CHRIS ADD THINGS TO THE LANG GEN NOW")
	}

	override fun getNameAdditional(): String = "(with automatic naming)"
}