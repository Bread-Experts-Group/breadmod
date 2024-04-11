package breadmod.registry.screen

import breadmod.BreadMod
import breadmod.BreadMod.modTranslatable
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTabs {
    val deferredRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreadMod.ID)
    @Suppress("unused")
    val MAIN_TAB: RegistryObject<CreativeModeTab> = deferredRegister.register("main_tab") {
        CreativeModeTab.builder()
            .withSearchBar()
            .title(modTranslatable("itemGroup", "main"))
            .displayItems { pParameters, pOutput ->
                pOutput.acceptAll(ModItems.deferredRegister.entries.map { it.get().defaultInstance })
            }
            .icon { ModBlocks.BREAD_BLOCK.get().defaultInstance }
            .build()
    }
}