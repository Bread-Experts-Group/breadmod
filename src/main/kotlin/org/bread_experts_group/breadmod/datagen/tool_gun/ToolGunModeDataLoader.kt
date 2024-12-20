package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
object ToolGunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), TOOL_GUN_DEF) {
	override fun apply(
		pObject : MutableMap<ResourceLocation, JsonElement>,
		pResourceManager : ResourceManager,
		pProfiler : ProfilerFiller
	) {
	}
}