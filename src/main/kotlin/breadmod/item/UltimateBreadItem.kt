package breadmod.item

import breadmod.registry.ModConfiguration
import breadmod.registry.item.ModItems
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import java.awt.Color
import java.lang.Math.random
import kotlin.math.roundToInt

open class UltimateBreadItem: Item(Properties().stacksTo(1).fireResistant()) {
    private fun getTimeLeft(pStack: ItemStack): Float = pStack.orCreateTag.getFloat(TIME_LEFT_NBT)
    fun setTimeLeft(pStack: ItemStack, ticks: Float) = pStack.orCreateTag.putFloat(TIME_LEFT_NBT, ticks)

    override fun getBarColor(pStack: ItemStack): Int = Color.HSBtoRGB(
        getTimeLeft(pStack) / MAX_TIME_CONFIG.get(),
        1.0f, 1.0f
    )
    override fun getBarWidth(pStack: ItemStack): Int = (13F * (getTimeLeft(pStack) / MAX_TIME_CONFIG.get())).roundToInt()

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pEntity is ServerPlayer) {
            if(pIsSelected) {
                val randomIndex = (random() * pEntity.inventory.items.size).roundToInt()
                val randomStack = pEntity.inventory.getItem(randomIndex).copyAndClear()
                pEntity.inventory.setItem(randomIndex, pStack)
                pEntity.inventory.setItem(pSlotId, randomStack)
            }

            if(pEntity.inventory.countItem(ModItems.ULTIMATE_BREAD.get()) > 1) {
                pEntity.setGameMode(GameType.DEFAULT_MODE)
                pEntity.inventory.items.forEach { if(it.`is`(ModItems.ULTIMATE_BREAD.get())) it.count = 0 }
                pLevel.explode(null, pEntity.x, pEntity.y, pEntity.z, 5.0f, Level.ExplosionInteraction.MOB)
            }

            val timeLeft = getTimeLeft(pStack)
            if (timeLeft <= 1) {
                pEntity.setGameMode(GameType.DEFAULT_MODE)
                pStack.count = 0
            } else {
                if(!pEntity.gameMode.isCreative) pEntity.setGameMode(GameType.CREATIVE)
                setTimeLeft(pStack, timeLeft - 1)
            }
        }
    }

    override fun getEntityLifespan(itemStack: ItemStack, level: Level): Int = 0

    companion object {
        val MAX_TIME_CONFIG = ModConfiguration.COMMON.ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS
        const val TIME_LEFT_NBT = "ticksLeft"
    }
}