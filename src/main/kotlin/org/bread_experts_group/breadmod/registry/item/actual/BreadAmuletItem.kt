package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.ModConfiguration.COMMON
import org.bread_experts_group.breadmod.util.itemTooltip
import java.text.DecimalFormat

class BreadAmuletItem(durability: Int) : Item(Properties().durability(durability)) {
	private companion object {
		val decimalFormat: DecimalFormat = DecimalFormat("0.#")
		val timers: MutableMap<String, PlayerData> = mutableMapOf()
	}

	data class PlayerData(var timeLeft: Int, var lastExec: Int)

	private fun playerFood(stack: ItemStack, player: Player) {
		val feedTime = COMMON.breadAmuletFeedTimeTicks.get()
		val feedAmount = COMMON.breadAmuletFeedAmount.get()
		val feedStacks = COMMON.breadAmuletEffectCanStack.get()
		val timer = Companion.timers.getOrPut(player.stringUUID) { PlayerData(feedTime, 0) }
		if (feedStacks || timer.lastExec != player.tickCount && !player.isCreative) {
			val hungerLevel = player.foodData.foodLevel
			if (hungerLevel <= 20 && timer.timeLeft <= 0) {
				stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack))
				player.foodData.foodLevel += feedAmount
				timer.timeLeft = feedTime
			} else if (hungerLevel <= 19) timer.timeLeft--
			timer.lastExec = player.tickCount
		}
	}

	override fun inventoryTick(
		stack: ItemStack,
		level: Level,
		entity: Entity,
		slotId: Int,
		isSelected: Boolean
	): Unit =
		if (entity is Player) this.playerFood(stack, entity) else {
		}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val secDelay = COMMON.breadAmuletFeedTimeTicks.get().toDouble() / 20
		val bars = COMMON.breadAmuletFeedAmount.get().toDouble() / 2
		val feedStacks = COMMON.breadAmuletEffectCanStack.get()
		tooltipComponents.add(
			this.itemTooltip(
				args = listOf(
					if (bars == 1.0) "a bar" else "${Companion.decimalFormat.format(bars)} bars",
					if (secDelay == 1.0) "second" else "${Companion.decimalFormat.format(secDelay)} seconds"
				)
			).append(
				if (feedStacks) Component.literal(" ").append(modTranslatable("item", "bread_amulet", "stacks"))
				else Component.empty()
			).withStyle(ChatFormatting.GOLD)
		)
	}
}