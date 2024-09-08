package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.tag.ModTags
import bread.mod.breadmod.datagen.tag.SmartTagProvider
import bread.mod.breadmod.datagen.tag.TagTypes
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.util.concurrent.CompletableFuture

class SmartBlockTagsProviderNeoForge(
    modID: String, val forClassLoader: ClassLoader, val forPackage: Package
) : SmartTagProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: GatherDataEvent) {
        forEvent.generator.addProvider(true, ModBlockTags(
            forEvent.generator.packOutput,
            forEvent.lookupProvider, modID,
            forEvent.existingFileHelper
        ))
    }

    inner class ModBlockTags(
        output: PackOutput,
        lookup: CompletableFuture<HolderLookup.Provider>,
        modID: String,
        helper: ExistingFileHelper
    ) : BlockTagsProvider(output, lookup, modID, helper) {
        override fun addTags(provider: HolderLookup.Provider) = getBlockTags().forEach { (block, annotation) ->
            annotation.types.forEach { tagType ->
                when (tagType) {
                    TagTypes.MINEABLE_WITH_HOE -> tag(BlockTags.MINEABLE_WITH_HOE).add(block)
                    TagTypes.MINEABLE_WITH_PICKAXE -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block)
                    else -> null
                }
            }
        }
    }

    inner class SmartItemTagProviderNeoForge() : SmartTagProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
        override fun generate(forEvent: GatherDataEvent) {
            forEvent.generator.addProvider(true, object : ItemTagsProvider(
                forEvent.generator.packOutput,
                forEvent.lookupProvider,
                ModBlockTags(
                    forEvent.generator.packOutput,
                    forEvent.lookupProvider, modID,
                    forEvent.existingFileHelper
                ).contentsGetter()
            ) {
                override fun addTags(provider: HolderLookup.Provider) = getItemTags().forEach { (item, annotation) ->
                    annotation.types.forEach { tagType ->
                        when (tagType) {
                            TagTypes.KNIFE -> tag(ModTags.KNIVES).add(item)
                            else -> null
                        }
                    }
                }
            })
        }
    }

    fun generateTags(event: GatherDataEvent) {
        // todo outputs nothing
        generate(event)
        // todo locks up when it reaches this provider (says it's starting provider for vanilla?)
        SmartItemTagProviderNeoForge().generate(event)
    }
}