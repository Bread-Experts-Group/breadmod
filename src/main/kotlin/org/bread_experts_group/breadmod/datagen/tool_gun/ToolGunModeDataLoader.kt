package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.bread_experts_group.breadmod.registry.Registry.logger
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunMode
import org.bread_experts_group.breadmod.util.jsonToComponent
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@Internal
object ToolGunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), TOOL_GUN_DEF) {
	private val loadedModes : MutableMap<String, MutableMap<String, Pair<Pair<Component, Component>, ToolGunMode>>> =
		mutableMapOf()
	val modes : Map<String, Map<String, Pair<Pair<Component, Component>, ToolGunMode>>>
		get() = this.loadedModes

	override fun apply(
		`object` : MutableMap<ResourceLocation, JsonElement>,
		resourceManager : ResourceManager,
		profiler : ProfilerFiller
	) {
		profiler.push("Load tool gun data")
		this.load(`object`)
		profiler.pop()
	}

	fun load(`object` : Map<ResourceLocation, JsonElement>) {
		`object`.forEach { (location, data) ->
			if (location.path.startsWith("mode/")) {
				try {
					val dataObj = data.asJsonObject
					val classSet = this.loadedModes.getOrPut(location.namespace, ::mutableMapOf)
					val loadedClass =
						Thread.currentThread().contextClassLoader.loadClass(
							dataObj.getAsJsonPrimitive("class").asString
						).kotlin
					if (loadedClass.isSubclassOf(ToolGunMode::class)) {
						val classConstructor = loadedClass.primaryConstructor ?: return@forEach
						classConstructor.isAccessible = true
						classSet[location.path.substringAfter("mode/")] =
							jsonToComponent(dataObj.getAsJsonObject("display_name")) to
									jsonToComponent(dataObj.getAsJsonObject("tooltip")) to
									classConstructor.call() as ToolGunMode
						classConstructor.isAccessible = false
					} else throw IllegalArgumentException("Class parameter for tool gun mode $location is invalid. Loaded an instance of ${loadedClass.qualifiedName}, expected a subclass of ${ToolGunMode::class.qualifiedName}")
				} catch (e : ClassNotFoundException) {
					logger.error(e)
				}
			}
		}
	}
}