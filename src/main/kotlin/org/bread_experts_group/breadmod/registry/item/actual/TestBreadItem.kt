package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable

class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).rarity(Rarity.EPIC)) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(modTranslatable("item", "test_bread", "tooltip").withStyle(ChatFormatting.GOLD))
        tooltipComponents.add(Component.literal("Hold this item to show the camera overlay (temp)"))
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val cap = context.level.getCapability(Capabilities.FluidHandler.BLOCK, context.clickedPos, context.clickedFace)
        if (!context.level.isClientSide && cap != null) {
            println("Fluid capability found at ${context.clickedPos}, Direction ${context.clickedFace}, hashcode ${cap.hashCode()}")
        }
        return InteractionResult.CONSUME
    }

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        return InteractionResult.PASS
    }
}