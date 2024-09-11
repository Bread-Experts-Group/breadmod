package bread.mod.breadmod.datagen.tag

import bread.mod.breadmod.reflection.LibraryScanner
import bread.mod.breadmod.util.ensureRegistrySupplier
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KProperty1


/**
 * An annotation-based tag provider.
 *
 * @property modID The mod ID to save tag definitions for.
 *
 * @see bread.mod.breadmod.datagen.tag
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartTagProvider(
    val modID: String, forClassLoader: ClassLoader, forPackage: Package
) {
    private val scanner: LibraryScanner = LibraryScanner(forClassLoader, forPackage)

    private fun getTagMap(): Map<RegistrySupplier<*>, Pair<Array<DataGenerateTag>, KProperty1<*, *>>> = buildMap {
        listOf(scanner.getObjectPropertiesAnnotatedWith<DataGenerateTag>()).forEach {
            it.forEach { (property, data) ->
                put(data.first.ensureRegistrySupplier(property), Pair(data.second, property))
            }
        }
    }

    /**
     * Generates tag definition files according to annotations in [bread.mod.breadmod.datagen.tag]
     * use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun getProvider(
        packOutput: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>
    ): List<TagsProvider<Any>> {
        val registryMap = mutableMapOf<ResourceKey<*>, MutableList<Pair<DataGenerateTag, RegistrySupplier<*>>>>()
        getTagMap().forEach { (register, data) ->
            data.first.forEach { a ->
                registryMap
                    .getOrPut(ResourceKey.createRegistryKey<Any>(ResourceLocation.parse(a.registryName))) { mutableListOf() }
                    .add(a to register)
            }
        }
        return registryMap.map { (registry, list) ->
            val makeItWork = registry as ResourceKey<out Registry<Any>>
            object : TagsProvider<Any>(packOutput, makeItWork, lookupProvider) {
                override fun addTags(provider: HolderLookup.Provider) = list.forEach {
                    tag(TagKey.create(makeItWork, ResourceLocation.parse(it.first.tag)))
                        .addOptional(it.second.id)
                }
            }
        }
    }
}
