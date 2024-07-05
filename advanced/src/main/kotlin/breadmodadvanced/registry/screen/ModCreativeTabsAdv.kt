package breadmodadvanced.registry.screen

import breadmodadvanced.ModMainAdv
import breadmodadvanced.ModMainAdv.modTranslatable
import breadmodadvanced.registry.block.ModBlocksAdv
import breadmodadvanced.registry.item.ModItemsAdv
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTabsAdv {
    internal val deferredRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModMainAdv.ID)

    val MAIN_TAB: RegistryObject<CreativeModeTab> = deferredRegister.register("main") {
        CreativeModeTab.builder()
            .title(modTranslatable("itemGroup", "main"))
            .displayItems { _, pOutput ->
                ModItemsAdv.deferredRegister.entries.forEach {
                    val item = it.get()
                    pOutput.accept(item.defaultInstance)
                }
            }
            .icon { ModBlocksAdv.DIESEL_GENERATOR.get().defaultInstance }
            .build()
    }
}