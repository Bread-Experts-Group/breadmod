package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput

@LanguageDataGenerator
internal class EnglishUSLangProvider(
    output: PackOutput
) : BaseLanguageProvider(output, "en_us") {
    private fun String.joinUnderscoreWithCaps() =
        this.split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

    override fun assureName(name: String, otherwise: String) =
        if (name == "<null>") otherwise.substringAfterLast('.').joinUnderscoreWithCaps() else name

    override fun getNameAdditional(): String = "(with automatic naming)"
}