package breadmod.datagen.tag

import breadmod.ModMain
import breadmod.registry.fluid.ModFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.tags.FluidTags
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModFluidTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : FluidTagsProvider(output, lookupProvider, ModMain.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(FluidTags.WATER)
            .add(ModFluids.BREAD_LIQUID.source.get())
    }
}