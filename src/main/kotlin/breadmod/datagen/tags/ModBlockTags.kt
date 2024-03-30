package breadmod.datagen.tags

import breadmod.BreadMod
import breadmod.block.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTags(
    output: PackOutput?,
    lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output!!, lookupProvider!!, BreadMod.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(ModBlocks.BREAD_BLOCK)
    }
}