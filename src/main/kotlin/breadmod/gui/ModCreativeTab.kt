package breadmod.gui

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTab {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create<CreativeModeTab>(Registries.CREATIVE_MODE_TAB, BreadMod.ID)

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    val BREADMOD_TAB: RegistryObject<CreativeModeTab> = CREATIVE_MODE_TABS.register<CreativeModeTab>(
        "example_tab"
    ) {
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { ModItems.BREAD_BLOCK_ITEM.defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                for (items in ModItems.REGISTRY.entries) {
                    output.accept(items.get())
                }
            }
            .title(Component.translatable(BreadMod.ID + ".itemGroup"))
            .build()
    } /*
    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(ModItems.BREAD_BLOCK_ITEM);
    }
 */
}