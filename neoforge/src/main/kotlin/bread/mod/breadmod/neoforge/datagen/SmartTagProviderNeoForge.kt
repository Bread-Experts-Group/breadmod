package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.tag.DataGenerateTag
import bread.mod.breadmod.datagen.tag.SmartTagProvider
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based tag provider.
 *
 * @property modID The mod ID to save tag definitions for.
 *
 * @see bread.mod.breadmod.datagen.tag
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartTagProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartTagProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates tag definition files according to annotations in [bread.mod.breadmod.datagen.tag]
     * use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    override fun generate(forEvent: GatherDataEvent) {
        val registryMap = mutableMapOf<ResourceKey<*>, MutableList<Pair<DataGenerateTag, RegistrySupplier<*>>>>()
        getTagMap().forEach { (register, data) ->
            data.first.forEach { a ->
                registryMap
                    .getOrPut(ResourceKey.createRegistryKey<Any>(ResourceLocation.parse(a.registryName))) { mutableListOf() }
                    .add(a to register)
            }
        }
        registryMap.forEach { (registry, list) ->
            val makeItWork = registry as ResourceKey<out Registry<Any>>
            forEvent.generator.addProvider(
                true,
                object : IntrinsicHolderTagsProvider<Any>(
                    forEvent.generator.packOutput, makeItWork, forEvent.lookupProvider,
                    {
                        when (it) {
                            is Block -> it.builtInRegistryHolder().key()
                            is Item -> it.builtInRegistryHolder().key()
                            else -> throw IllegalArgumentException("Unknown intrinsic holder type: $it")
                        } as ResourceKey<Any>
                    }, modID, forEvent.existingFileHelper
                ) {
                    override fun addTags(provider: HolderLookup.Provider) = list.forEach {
                        tag(TagKey.create(makeItWork, ResourceLocation.parse(it.first.tag)))
                            .add(it.second.get())
                    }
                }
            )
        }
    }
}
