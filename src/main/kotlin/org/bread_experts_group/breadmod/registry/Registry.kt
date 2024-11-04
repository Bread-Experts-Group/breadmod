package org.bread_experts_group.breadmod.registry

import net.neoforged.bus.api.IEventBus
import org.bread_experts_group.breadmod.Breadmod.Companion.LOGGER
import org.bread_experts_group.breadmod.item.armor.ModArmorMaterials
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.fluid.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.sound.ModSounds

object Registry {
    private val registerList = listOf(
        ModItems.ITEM_REGISTRY,
        ModBlocks.BLOCK_REGISTRY,
        ModSounds.SOUND_REGISTRY,
        ModBlockEntityTypes.BLOCK_ENTITY_REGISTRY,
        ModCreativeTabs.CREATIVE_TAB_REGISTRY,
        ModFluids.FLUID_REGISTRY,
        ModFluids.FLUID_TYPE_REGISTRY,
        ModEntityTypes.ENTITY_REGISTRY,
        ModArmorMaterials.ARMOR_REGISTRY,
        ModDataComponents.DATA_COMPONENT_REGISTRY,
        ModRecipeSerializers.RECIPE_SERIALIZER_REGISTRY,
        ModRecipeTypes.RECIPE_TYPE_REGISTRY,
        ModMenuTypes.MENU_TYPE_REGISTRY
    )

    fun registerAll(bus: IEventBus) {
        registerList.forEach {
            LOGGER.info("Pushing register for ${it.registryName}")
            it.register(bus)
        }
    }
}