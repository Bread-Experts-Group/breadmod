package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunMode
import org.bread_experts_group.breadmod.util.componentToJson
import java.util.concurrent.CompletableFuture

abstract class ToolGunModeProvider(private val packOutput : PackOutput, private val modID : String) : DataProvider {
	private val addedModes : MutableMap<String, Pair<Pair<Component, Component>, Class<*>>> = mutableMapOf()
	abstract fun addModes()
	override fun run(output : CachedOutput) : CompletableFuture<*> {
		this.addModes()
		val dataLocation =
			this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modID)
				.resolve(Companion.TOOL_GUN_DEF).resolve("mode")
		return CompletableFuture.allOf(
			*buildList {
				this@ToolGunModeProvider.addedModes.forEach { (name, data) ->
					this.add(DataProvider.saveStable(output, JsonObject().also {
						it.add(Companion.DISPLAY_NAME_KEY, componentToJson(data.first.first))
						it.add(Companion.TOOLTIP_KEY, componentToJson(data.first.second))
						it.addProperty(Companion.CLASS_KEY, data.second.javaClass.kotlin.qualifiedName)
					}, dataLocation.resolve("$name.json")))
				}
			}.toTypedArray()
		)
	}

	fun <T : ToolGunMode> addMode(
		name : String,
		displayName : Component,
		tooltip : Component,
		actionClass : Class<T>
	) {
		check(!this.addedModes.containsKey(name)) { "There already exists a tool gun mode for $this.modID/$name!" }
		this.addedModes[name] = displayName to tooltip to actionClass
	}

	override fun getName() : String = "Toolgun Modes: ${this.modID}"

	private companion object {
		const val TOOL_GUN_DEF : String = "tool_gun"
		//		const val CONTROLS_ID_KEY : String = "id"
//		const val CONTROLS_NAME_TRANSLATION_KEY : String = "controls_name_key"
//		const val CONTROLS_CATEGORY_TRANSLATION_KEY : String = "controls_category_key"
//		const val TOOLGUN_INFO_DISPLAY_KEY : String = "${this.TOOL_GUN_DEF}_key"
//		const val KEY_ENTRY_KEY : String = "key"
//		const val MODIFIER_ENTRY_KEY : String = "modifier"
//		const val KEYBINDS_KEY : String = "keybinds"
		const val CLASS_KEY : String = "class"
		const val DISPLAY_NAME_KEY : String = "display_name"
		const val TOOLTIP_KEY : String = "tooltip"
	}
}