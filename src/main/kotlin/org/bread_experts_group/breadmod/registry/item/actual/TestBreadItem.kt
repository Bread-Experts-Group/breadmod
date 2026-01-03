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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.util.itemTooltip
import org.bread_experts_group.breadmod.util.logDebugInfo

class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).rarity(Rarity.EPIC)) {
	val logger: Logger = LogManager.getLogger("Test Bread Item")
	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(this.itemTooltip().withStyle(ChatFormatting.GOLD))
		tooltipComponents.add(Component.literal("Hold this item to show the camera overlay (temp)"))
	}

	override fun useOn(context: UseOnContext): InteractionResult {
//		val cap = context.level.getCapability(Capabilities.FluidHandler.BLOCK, context.clickedPos, context.clickedFace)
//		if (!context.level.isClientSide && cap != null) this.logger.info(
//			"Fluid capability found {}, Direction: {}, Hashcode: {}",
//			context.clickedPos, context.clickedFace.name, cap.hashCode()
//		)
		val state = context.level.getBlockState(context.clickedPos)
		logDebugInfo("TestBreadItem[${context.level}, ${context.clickedPos}, ${context.player}, $state]")
		return InteractionResult.PASS
	}

//	override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
//		val pos = context.clickedPos.above()
//		val fakePlayer = FakePlayer(context.level, pos, context.player)
//		context.level.addFreshEntity(fakePlayer)
//		return InteractionResult.PASS
//	}
}