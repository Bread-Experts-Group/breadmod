package breadmod.datagen.toolgun

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
internal object ModToolgunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), "toolgun") {
    private val loadedActions: MutableMap<String, List<IToolgunMode>> = mutableMapOf()
    val actions: Map<String, List<IToolgunMode>> = loadedActions

    override fun apply(
        pObject: MutableMap<ResourceLocation, JsonElement>,
        pResourceManager: ResourceManager,
        pProfiler: ProfilerFiller
    ) {
        println("TLGN DATA LOADER::: !!! :::")
        TODO("Not yet implemented")
    }
}