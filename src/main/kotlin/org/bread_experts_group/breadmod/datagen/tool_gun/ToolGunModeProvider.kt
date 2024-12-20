package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.bread_experts_group.breadmod.util.componentToJson
import java.util.concurrent.CompletableFuture

const val TOOL_GUN_DEF : String = "tool_gun"
const val CONTROLS_ID_KEY : String = "id"
const val CONTROLS_NAME_TRANSLATION_KEY : String = "controls_name_key"
const val CONTROLS_CATEGORY_TRANSLATION_KEY : String = "controls_category_key"
const val TOOLGUN_INFO_DISPLAY_KEY : String = "${TOOL_GUN_DEF}_key"
const val KEY_ENTRY_KEY : String = "key"
const val MODIFIER_ENTRY_KEY : String = "modifier"
const val KEYBINDS_KEY : String = "keybinds"
const val CLASS_KEY : String = "class"
const val DISPLAY_NAME_KEY : String = "display_name"
const val TOOLTIP_KEY : String = "tooltip"

abstract class ToolGunModeProvider(private val packOutput : PackOutput, private val modID : String) : DataProvider {
	data class Control(
		val id : String,
		val nameKey : String,
		val categoryKey : String,
		val toolGunComponent : Component,
		val key : String,
		val modifier : String = "none"
	) {
		companion object {
			val CODEC : StreamCodec<RegistryFriendlyByteBuf, Control> = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, Control::id,
				ByteBufCodecs.STRING_UTF8, Control::nameKey,
				ByteBufCodecs.STRING_UTF8, Control::categoryKey,
				ComponentSerialization.STREAM_CODEC, Control::toolGunComponent,
				ByteBufCodecs.STRING_UTF8, Control::key,
				ByteBufCodecs.STRING_UTF8, Control::modifier,
				ToolGunModeProvider::Control
			)
			val EMPTY : Control = Control("breadmod", "empty", "none", Component.empty(), "none")
		}
	}

	private val addedModes : MutableMap<String, Triple<Pair<Component, Component>, List<Control>, Class<*>>> =
		mutableMapOf()

	override fun run(output : CachedOutput) : CompletableFuture<*> {
		val dataLocation =
			this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modID)
				.resolve(TOOL_GUN_DEF).resolve("mode")
		return CompletableFuture.allOf(
			*buildList {
				this@ToolGunModeProvider.addedModes.forEach { (name, data) ->
					this.add(DataProvider.saveStable(output, JsonObject().also {
						it.add(DISPLAY_NAME_KEY, componentToJson(data.first.first))
						it.add(TOOLTIP_KEY, componentToJson(data.first.second))
						it.add(KEYBINDS_KEY, JsonArray().also { array ->
							data.second.forEach {
								array.add(JsonObject().also { keyObj ->
									keyObj.addProperty(CONTROLS_ID_KEY, it.id)
									keyObj.addProperty(KEY_ENTRY_KEY, it.key)
									keyObj.addProperty(MODIFIER_ENTRY_KEY, it.modifier)
									keyObj.addProperty(CONTROLS_NAME_TRANSLATION_KEY, it.nameKey)
									keyObj.addProperty(CONTROLS_CATEGORY_TRANSLATION_KEY, it.categoryKey)
									keyObj.add(
										TOOLGUN_INFO_DISPLAY_KEY,
										componentToJson(it.toolGunComponent)
									)
								})
							}
						})
						it.addProperty(CLASS_KEY, data.third.kotlin.qualifiedName)
					}, dataLocation.resolve("$name.json")))
				}
			}.toTypedArray()
		)
	}

	override fun getName() : String = "Toolgun Modes: ${this.modID}"
}