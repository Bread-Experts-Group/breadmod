package breadmod.screen

import breadmod.BreadMod
import breadmod.BreadMod.modTranslatable
import breadmod.registry.block.ModBlocks.BREAD_BLOCK
import breadmod.registry.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object MainTab {
    val deferredRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreadMod.ID)

    val MAIN_TAB: RegistryObject<CreativeModeTab> = deferredRegister.register("main_tab") { // TODO: Separate items to their own appropriate tabs
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { BREAD_BLOCK.get().defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                for (items in ModItems.deferredRegister.entries) output.accept(items.get())
            }
            .title(modTranslatable("itemGroup", "main"))
            .build()
    }
}