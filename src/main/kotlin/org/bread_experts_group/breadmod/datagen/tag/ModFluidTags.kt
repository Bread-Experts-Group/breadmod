package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.tags.FluidTags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.util.add
import java.util.concurrent.CompletableFuture

class ModFluidTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : FluidTagsProvider(output, lookupProvider, BreadMod.ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(FluidTags.WATER)
            .add(ModFluids.BREAD_LIQUID.source)
    }
}