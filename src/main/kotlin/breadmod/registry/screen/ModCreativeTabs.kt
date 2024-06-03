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
                    when {
                        item is RegisterSpecialCreativeTab -> if (item.creativeModeTabs.contains(registryObject))
                            if(item.displayInCreativeTab(pParameters, pOutput)) pOutput.accept(item.defaultInstance)
                        general -> pOutput.accept(item.defaultInstance)
                    }
                }
            }
        constructor(builder)
        return registryObject
    }

    val MAIN_TAB: RegistryObject<CreativeModeTab> = constructTab("main", true) {
        icon { ModBlocks.BREAD_BLOCK.get().defaultInstance }
        withSearchBar()
    }

    val SPECIALS_TAB: RegistryObject<CreativeModeTab> = constructTab("specials", false) {
        icon { ModItems.ULTIMATE_BREAD.get().defaultInstance }
        withTabsBefore(MAIN_TAB.key)
    }
}