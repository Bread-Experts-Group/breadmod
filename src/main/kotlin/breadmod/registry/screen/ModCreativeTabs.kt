package breadmod.registry.screen

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import breadmod.registry.item.RegisterSpecialCreativeTab
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTabs {
    val deferredRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModMain.ID)
    private fun constructTab(name: String, general: Boolean, constructor: CreativeModeTab.Builder.() -> Unit): RegistryObject<CreativeModeTab> {
        val builder = CreativeModeTab.builder()
        val registryObject = deferredRegister.register(name) { builder.build() }
        builder
            .title(modTranslatable("itemGroup", name))
            .displayItems { pParameters, pOutput ->
                ModItems.deferredRegister.entries.forEach {
                    val item = it.get()
                    if(item is RegisterSpecialCreativeTab)
                        if(item.creativeModeTabs.contains(registryObject)) item.displayInCreativeTab(pParameters, pOutput)
                    else if(general) pOutput.accept(item.defaultInstance)
                }
            }
        constructor(builder)
        return registryObject
    }

    val MAIN_TAB: RegistryObject<CreativeModeTab> = constructTab("main", true) {
        icon { ModBlocks.BREAD_BLOCK.get().defaultInstance }
        withSearchBar()
    }

    val CHRIS_TAB: RegistryObject<CreativeModeTab> = constructTab("chrisp", false) {
        icon { ModBlocks.GENERIC_POWER_INTERFACE.get().defaultInstance }
        withTabsBefore(MAIN_TAB.id)
    }

    val EXAMPLE_GENERAL_TAB: RegistryObject<CreativeModeTab> = constructTab("gen2", true) {
        icon { ModBlocks.MONITOR.get().defaultInstance }
        withTabsBefore(CHRIS_TAB.id)
    }
}