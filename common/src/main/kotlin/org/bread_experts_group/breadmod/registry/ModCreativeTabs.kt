package org.bread_experts_group.breadmod.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadmod.Breadmod

object ModCreativeTabs {
    val REGISTRY: RegistryProvider<CreativeModeTab> =
        BuiltInRegistries.CREATIVE_MODE_TAB.getProvider(Breadmod.MOD_ID)

    val TEST_TAB: RegistryObject<CreativeModeTab, CreativeModeTab> = REGISTRY.register("test_tab") {
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
            .displayItems { parameters, output ->
                output.accept(ModItems.TEST_ITEM.get())
                output.accept(ModBlocks.TEST_BLOCK.get())
            }
            .title(Component.literal("itemGroup.test_tab"))
            .icon { ModItems.TEST_ITEM.toStack() }
            .build()
    }
}