package breadmod.datagen.tag

import breadmod.ModMain
import breadmod.registry.entity.ModPainting.CLASSIFIED
import breadmod.registry.entity.ModPainting.DEVIL_PUPP
import breadmod.registry.entity.ModPainting.FISH
import breadmod.registry.entity.ModPainting.PAINTING_TEST
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.PaintingVariantTagsProvider
import net.minecraft.tags.PaintingVariantTags
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture
import breadmod.util.add

class ModPaintingTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : PaintingVariantTagsProvider(output, lookupProvider, ModMain.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(PaintingVariantTags.PLACEABLE)
            .add(PAINTING_TEST, DEVIL_PUPP, FISH, CLASSIFIED)
    }
}