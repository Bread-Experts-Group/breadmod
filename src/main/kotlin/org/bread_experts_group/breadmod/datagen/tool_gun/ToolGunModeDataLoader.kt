package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.bread_experts_group.breadmod.client.tool_gun_mode.TestScreen.Companion.modeWidgets
import org.bread_experts_group.breadmod.registry.Registry.logger
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunModeData
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
object ToolGunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), TOOL_GUN_DEF) {
	private val loadedModes : MutableMap<String, MutableMap<String, ToolGunModeData>> =
		mutableMapOf()
	val modes : Map<String, Map<String, ToolGunModeData>>
		get() = this.loadedModes

	override fun apply(
		`object` : MutableMap<ResourceLocation, JsonElement>,
		resourceManager : ResourceManager,
		profiler : ProfilerFiller
	) {
		profiler.startTick()
		profiler.push("Load tool gun data")
		this.load(`object`)
		profiler.pop()
		profiler.endTick()
	}

	private fun load(`object` : Map<ResourceLocation, JsonElement>) {
		this.loadedModes.clear()
		modeWidgets.clear()
		`object`.forEach { (location, data) ->
			if (location.path.startsWith("mode/")) {
				try {
					val modeData = ToolGunModeData.CODEC.parse(
						this.registryLookup.createSerializationContext(JsonOps.INSTANCE), data
					).result().get()
					val classSet = this.loadedModes.getOrPut(location.namespace, ::mutableMapOf)
					classSet[location.path.substringAfter("mode/")] = modeData
					modeWidgets.add(modeData.widget)
				} catch (e : ClassNotFoundException) {
					logger.error(e)
				}
			}
		}
	}
}