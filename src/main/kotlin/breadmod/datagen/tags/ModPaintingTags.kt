package breadmod.datagen.tags

import breadmod.BreadMod
import breadmod.registry.entity.ModPainting.DEVIL_PUPP
import breadmod.registry.entity.ModPainting.PAINTING_TEST
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.PaintingVariantTagsProvider
import net.minecraft.tags.PaintingVariantTags
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModPaintingTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : PaintingVariantTagsProvider(output, lookupProvider, BreadMod.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(PaintingVariantTags.PLACEABLE)
            .add(PAINTING_TEST.key!!)
            .add(DEVIL_PUPP.key!!)
    }
}