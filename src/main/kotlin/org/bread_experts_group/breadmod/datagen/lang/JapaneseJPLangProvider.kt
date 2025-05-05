package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable

@LanguageDataGenerator
internal class JapaneseJPLangProvider(
	output: PackOutput
) : BaseLanguageProvider(output, "ja_jp") {
	override fun assureName(name: String, languageID: String) =
		if (name == "<null>") throw UnsupportedOperationException("日本語の自動的な訳は作れません。")
		else name

	override fun addManualTranslations() {
//		this.bmAdd(modTranslatable("irc", "connected"), "Connected to host [%1\$s]")
		this.bmAdd(modTranslatable("irc", "disconnected"), "リモートホスト切断しました [%1\$s]")
//		this.bmAdd(modTranslatable("irc", "unknown_host"), "Cannot connect, unknown host \"%1\$s:%2\$s\"")
//		this.bmAdd(modTranslatable("irc", "timed_out"), "Cannot connect, timed out \"[%1\$s]:%2\$s\"")
//		this.bmAdd(
//			modTranslatable("irc", "connection_failed"),
//			"Cannot connect to host \"[%1\$s]:%2\$s\": [%3\$s] \"%4\$s\""
//		)
//		this.bmAdd(
//			modTranslatable("irc", "connection_failure"),
//			"Host [%1\$s] connection failure: [%2\$s] \"%3\$s\""
//		)
//		this.bmAdd(modTranslatable("tool_gun", "settings", "title"), "Tool Gun Setup Utility V1.0")
//		this.add("modmenu.nameTranslation.breadmod", "Bread Mod")
//		this.add("jei.breadmod.generic.recipe_time", "%ss")
	}

	override fun getNameAdditional(): String = "日本語 (Japanese)"
}