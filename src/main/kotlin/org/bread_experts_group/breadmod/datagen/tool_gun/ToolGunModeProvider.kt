package org.bread_experts_group.breadmod.datagen.tool_gun

import com.mojang.datafixers.util.Pair
import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidgetData
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunMode
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunModeData
import java.util.concurrent.CompletableFuture

abstract class ToolGunModeProvider(
	private val packOutput: PackOutput,
	private val lookupProvider: CompletableFuture<HolderLookup.Provider>,
	private val modID: String
) : DataProvider {
	private val addedModes: MutableMap<String, ToolGunModeData> = mutableMapOf()
	abstract fun addModes()
	override fun run(output: CachedOutput): CompletableFuture<*> =
		this.lookupProvider.thenCompose { this.run(output, it) }

	private fun run(output: CachedOutput, lookupProvider: HolderLookup.Provider): CompletableFuture<*> {
		this.addModes()
		val dataLocation =
			this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modID)
				.resolve(Companion.TOOL_GUN_DEF).resolve("mode")
		return CompletableFuture.allOf(
			*buildList {
				this@ToolGunModeProvider.addedModes.forEach { (name, data) ->
					this.add(
						DataProvider.saveStable(
							output,
							lookupProvider,
							ToolGunModeData.CODEC,
							data,
							dataLocation.resolve("$name.json")
						)
					)
				}
			}.toTypedArray()
		)
	}

	fun addMode(
		namespace: String,
		name: String,
		displayName: Component,
		tooltip: Component,
		actionClass: ToolGunMode,
		widget: ModeWidgetData
	) {
		check(!this.addedModes.containsKey(name)) { "There already exists a tool gun mode for $this.modID/$name!" }
		this.addedModes[name] =
			ToolGunModeData(Pair(namespace, name), displayName, tooltip, actionClass, widget)
	}

	/**
	 * Method for testing tool gun mode functionality
	 */
	fun addEmptyMode(): Unit = this.addMode(
		BreadMod.ID,
		"empty",
		Component.literal("empty"),
		Component.literal("empty"),
		EmptyMode(),
		ModeWidgetData.NONE
	)

	override fun getName(): String = "Toolgun Modes: ${this.modID}"

	companion object {
		const val TOOL_GUN_DEF: String = "tool_gun"

		//		const val CONTROLS_ID_KEY : String = "id"
//		const val CONTROLS_NAME_TRANSLATION_KEY : String = "controls_name_key"
//		const val CONTROLS_CATEGORY_TRANSLATION_KEY : String = "controls_category_key"
//		const val TOOLGUN_INFO_DISPLAY_KEY : String = "${this.TOOL_GUN_DEF}_key"
//		const val KEY_ENTRY_KEY : String = "key"
//		const val MODIFIER_ENTRY_KEY : String = "modifier"
//		const val KEYBINDS_KEY : String = "keybinds"
		const val CLASS_KEY: String = "action_class"
		const val DISPLAY_NAME_KEY: String = "display_name"
		const val TOOLTIP_KEY: String = "tooltip"
	}
}