package breadmod.compat.projecte

import breadmod.BreadMod
import breadmod.registry.item.ModItems
import moze_intel.projecte.api.data.CustomConversionProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import java.util.concurrent.CompletableFuture

class ModEMCProvider(packOutput: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>
) : CustomConversionProvider(packOutput, lookupProvider) {
    override fun addCustomConversions(pWriter: HolderLookup.Provider) {
        createConversionBuilder(BreadMod.modLocation("defaults"))
            .comment("Default EMC Values for Bread Mod")
            .before(ModItems.FLOUR.get(), 250)
    }
}