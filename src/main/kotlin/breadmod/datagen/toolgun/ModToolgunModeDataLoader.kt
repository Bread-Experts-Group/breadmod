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
                val loadedClass = ClassLoader.getSystemClassLoader().loadClass(data.asJsonObject.getAsJsonPrimitive("class").asString)
                if(IToolgunMode::class.java.isAssignableFrom(loadedClass)) {
                    val classConstructor = loadedClass.constructors.first()
                    classConstructor.isAccessible = true
                    classSet.add(classConstructor.newInstance() as IToolgunMode)
                    classConstructor.isAccessible = false
                } else throw IllegalArgumentException("Class parameter for toolgun mode $location is invalid. Loaded an instance of ${loadedClass.kotlin.qualifiedName}, expected a subclass of ${IToolgunMode::class.qualifiedName}")
            }
        }
        println("Here's the classes I loaded: $loadedModes")
        pProfiler.pop()
    }
}