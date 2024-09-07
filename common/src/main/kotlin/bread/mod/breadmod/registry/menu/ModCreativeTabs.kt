package bread.mod.breadmod.registry.menu

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.datagen.DataGenerateLanguage
import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.item.ModItems
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.Row

object ModCreativeTabs {
    internal val CREATIVE_TAB_REGISTRY: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(ModMainCommon.MOD_ID, Registries.CREATIVE_MODE_TAB)

    private fun constructTab(
        name: String,
        general: Boolean,
        constructor: CreativeModeTab.Builder.() -> Unit
    ): RegistrySupplier<CreativeModeTab> {
        val builder = CreativeModeTab.builder(Row.TOP, 1)
        val registryObject = CREATIVE_TAB_REGISTRY.register(name) { builder.build() }
        builder
            .title(modTranslatable("itemGroup", name))
            .displayItems { pParameters, pOutput ->
//                ModItems.ITEM_REGISTRY.entries.forEach {
//                    val item = it.get()
//                    when {
//                        item is IRegisterSpecialCreativeTab -> if (item.creativeModeTabs.contains(registryObject))
//                            if (item.displayInCreativeTab(pParameters, pOutput)) pOutput.accept(item.defaultInstance)
//
//                        general -> pOutput.accept(item.defaultInstance)
//                    }
//                }
                ModItems.ITEM_REGISTRY.forEach {
                    val item = it.get()
                    pOutput.accept(item.defaultInstance)
                }
            }
        constructor(builder)
        return registryObject
    }

//    val MAIN_TAB: RegistrySupplier<CreativeModeTab> = CREATIVE_TAB_REGISTRY.register("main_tab") {
//        CreativeTabRegistry.create(ModMainCommon.modTranslatable("main_tab")) {
//            ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance
//        }
//    }

    @DataGenerateLanguage("en_us", "The Bread Mod")
    val MAIN_TAB: RegistrySupplier<CreativeModeTab> = constructTab("main", true) {
        icon { ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance }
    }
}