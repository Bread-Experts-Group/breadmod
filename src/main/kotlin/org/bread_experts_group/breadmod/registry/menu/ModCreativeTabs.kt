package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.function.Supplier

object ModCreativeTabs {
    val CREATIVE_TAB_REGISTRY: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Breadmod.ID)

    private fun constructTab(
        name: String,
        general: Boolean,
        constructor: CreativeModeTab.Builder.() -> Unit
    ): Supplier<CreativeModeTab> {
        val builder = CreativeModeTab.builder()
        val registryObject = CREATIVE_TAB_REGISTRY.register(name) { -> builder.build() }
        builder
            .title(modTranslatable("itemGroup", name))
            .displayItems { parameters, output ->
                ModItems.ITEM_REGISTRY.entries.forEach {
                    val item = it.get()
                    when {
                        item is IRegisterSpecialCreativeTab -> if (item.creativeModeTabs.contains(registryObject))
                            if (item.displayInCreativeTab(parameters, output)) output.accept(item.defaultInstance)

                        general -> output.accept(item.defaultInstance)
                    }
                }
            }
        constructor(builder)
        return registryObject
    }

    val MAIN_TAB: Supplier<CreativeModeTab> = constructTab("main", true) {
        icon { ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance }
    }

    val SPECIALS_TAB: Supplier<CreativeModeTab> = constructTab("specials", false) {
        icon { ModBlocks.REINFORCED_BREAD_BLOCK.get().asItem().defaultInstance }
    }
}