package org.bread_experts_group.breadmod.item

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.lang.Math.random
import java.util.function.Supplier
import kotlin.math.roundToInt

class UltimateBreadItem : Item(
    Properties()
        .stacksTo(1)
        .fireResistant()
        .component(ModDataComponents.TIME_LEFT, 20 * 20)
), IRegisterSpecialCreativeTab {
    private fun getTimeLeft(stack: ItemStack): Long {
        if (stack.has(ModDataComponents.TIME_LEFT)) return stack.getOrDefault(
            ModDataComponents.TIME_LEFT,
            MAX_TIME_CONFIG.get()
        )
        else {
            val max = MAX_TIME_CONFIG.get()
            setTimeLeft(stack, max)
            return max
        }
    }

    fun setTimeLeft(stack: ItemStack, ticks: Long) = stack.set(ModDataComponents.TIME_LEFT, ticks)

    override fun getBarColor(stack: ItemStack): Int =
        (13F * (getTimeLeft(stack) / MAX_TIME_CONFIG.get())).roundToInt()

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity is ServerPlayer) {
            if (isSelected) {
                val randomIndex = (random() * entity.inventory.items.size).roundToInt()
                val randomStack = entity.inventory.getItem(randomIndex).copyAndClear()
                entity.inventory.setItem(randomIndex, stack)
                entity.inventory.setItem(slotId, randomStack)
            }

            if (entity.inventory.countItem(ModItems.ULTIMATE_BREAD.get()) > 1) {
                entity.setGameMode(GameType.DEFAULT_MODE)
                entity.inventory.items.forEach { if (it.`is`(ModItems.ULTIMATE_BREAD.get())) it.count = 0 }
                level.explode(entity, entity.x, entity.y, entity.z, 5.0f, Level.ExplosionInteraction.MOB)
            }

            val timeLeft = getTimeLeft(stack)
            if (timeLeft <= 1) {
                entity.setGameMode(GameType.DEFAULT_MODE)
                stack.count = 0
            } else {
                if (!entity.gameMode.isCreative) entity.setGameMode(GameType.CREATIVE)
                setTimeLeft(stack, timeLeft - 1)
            }
        }
    }

    override fun getEntityLifespan(itemStack: ItemStack, level: Level): Int = 0

    override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    companion object {
        val MAX_TIME_CONFIG = ModConfiguration.COMMON.ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS
    }
}