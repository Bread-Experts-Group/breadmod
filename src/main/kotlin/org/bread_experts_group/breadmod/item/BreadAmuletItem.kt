package org.bread_experts_group.breadmod.item

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
import java.text.DecimalFormat

class BreadAmuletItem(durability: Int) : Item(Properties().durability(durability)) {
    companion object {
        private val feedTime = COMMON.BREAD_AMULET_FEED_TIME_TICKS
        private val feedAmt = COMMON.BREAD_AMULET_FEED_AMOUNT
        private val feedStacks = COMMON.BREAD_AMULET_STACKS

        private val decimalFormat = DecimalFormat("0.#")

        data class PlayerData(var timeLeft: Int, var lastExec: Int)

        private val timers = mutableMapOf<String, PlayerData>()
    }

    private fun playerFood(stack: ItemStack, player: Player) {
        val timer = timers.getOrPut(player.stringUUID) { PlayerData(feedTime.get(), 0) }
        if (feedStacks.get() || timer.lastExec != player.tickCount && !player.isCreative) {
            val hungerLevel = player.foodData.foodLevel
            if (hungerLevel <= 20 && timer.timeLeft <= 0) {
                stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack))
                player.foodData.foodLevel += feedAmt.get()
                timer.timeLeft = feedTime.get()
            } else if (hungerLevel <= 19) timer.timeLeft--
            timer.lastExec = player.tickCount
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) =
        if (entity is Player) playerFood(stack, entity) else {
        }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val bars = feedAmt.get().toDouble() / 2
        val secDelay = feedTime.get().toDouble() / 20
        tooltipComponents.add(
            modTranslatable(
                "item",
                "bread_amulet", "tooltip",
                args = listOf(
                    if (bars == 1.0) "a bar" else "${decimalFormat.format(bars)} bars",
                    if (secDelay == 1.0) "second" else "${decimalFormat.format(secDelay)} seconds"
                )
            ).append(
                if (feedStacks.get()) Component.literal(" ").append(modTranslatable("item", "bread_amulet", "stacks"))
                else Component.empty()
            ).withStyle(ChatFormatting.GOLD)
        )
    }
}