package breadmod.gui

import breadmod.BreadMod
import breadmod.block.registry.ModBlocks.BREAD_BLOCK
import breadmod.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTab {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreadMod.ID)

    val BREADMOD_TAB: RegistryObject<CreativeModeTab> = REGISTRY.register("main_tab") {
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { BREAD_BLOCK.get().defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                for (items in ModItems.REGISTRY.entries) {
                    output.accept(items.get())
                }
            }
            .title(Component.translatable(BreadMod.ID + ".itemGroup"))
            .build()
    }
}