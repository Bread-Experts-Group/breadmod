package breadmod.datagen.toolgun

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.full.primaryConstructor

@Internal
internal object ModToolgunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), "toolgun") {
    private val loadedModes: MutableMap<String, MutableSet<IToolgunMode>> = mutableMapOf()
    val modes: Map<String, Set<IToolgunMode>> = loadedModes

    override fun apply(
        pObject: MutableMap<ResourceLocation, JsonElement>,
        pResourceManager: ResourceManager,
        pProfiler: ProfilerFiller
    ) {
        pProfiler.push("Load toolgun data")
        pObject.forEach { (location, data) ->
            if(location.path.startsWith("mode/")) {
                val classSet = loadedModes.getOrPut(location.namespace) { mutableSetOf() }
                val loadedClass = ClassLoader.getSystemClassLoader().loadClass(data.asJsonObject.getAsJsonPrimitive("class").asString).kotlin
                if(loadedClass.java.isAssignableFrom(IToolgunMode::class.java)) {
                    classSet.add(loadedClass.primaryConstructor!!.call() as IToolgunMode)
                } else throw IllegalArgumentException("Class parameter for toolgun mode $location is invalid. Loaded an instance of ${loadedClass.qualifiedName}, expected a subclass of ${IToolgunMode::class.qualifiedName}")
            }
        }
        println("Here's the classes I loaded: $loadedModes")
        pProfiler.pop()
    }
}