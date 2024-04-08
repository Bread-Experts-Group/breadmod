package breadmod.screens.creative

import breadmod.BreadMod
import breadmod.BreadMod.modTranslatable
import breadmod.block.registry.ModBlocks.BREAD_BLOCK
import breadmod.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object MainTab {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreadMod.ID)

    val MAIN_TAB: RegistryObject<CreativeModeTab> = REGISTRY.register("main_tab") {
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { BREAD_BLOCK.get().defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                for (items in ModItems.REGISTRY.entries) {
                    output.accept(items.get())
                }
            }
            .title(modTranslatable("itemGroup", "main"))
            .build()
    }
}