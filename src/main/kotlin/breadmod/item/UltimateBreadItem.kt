package breadmod.item

import breadmod.registry.ModConfiguration
import breadmod.registry.item.ModItems
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.menu.ModCreativeTabs
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import net.minecraftforge.registries.RegistryObject
import java.awt.Color
import java.lang.Math.random
import kotlin.math.min
import kotlin.math.roundToInt

class UltimateBreadItem : Item(Properties().stacksTo(1).fireResistant()), IRegisterSpecialCreativeTab {
    private fun getTimeLeft(pStack: ItemStack): Long {
        if (pStack.orCreateTag.contains(TIME_LEFT_NBT)) return pStack.orCreateTag.getLong(TIME_LEFT_NBT)
        else {
            val max = MAX_TIME_CONFIG.get()
            setTimeLeft(pStack, max)
            return max
        }
    }

    fun setTimeLeft(pStack: ItemStack, ticks: Long) = pStack.orCreateTag.putLong(TIME_LEFT_NBT, ticks)

    override fun getBarColor(pStack: ItemStack): Int = Color.HSBtoRGB(
        MAX_TIME_CONFIG.get().let { min(it, getTimeLeft(pStack)).toFloat() / it },
        1.0f, 1.0f
    )

    override fun getBarWidth(pStack: ItemStack): Int =
        (13F * (getTimeLeft(pStack) / MAX_TIME_CONFIG.get())).roundToInt()

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if (pEntity is ServerPlayer) {
            if (pIsSelected) {
                val randomIndex = (random() * pEntity.inventory.items.size).roundToInt()
                val randomStack = pEntity.inventory.getItem(randomIndex).copyAndClear()
                pEntity.inventory.setItem(randomIndex, pStack)
                pEntity.inventory.setItem(pSlotId, randomStack)
            }

            if (pEntity.inventory.countItem(ModItems.ULTIMATE_BREAD.get()) > 1) {
                pEntity.setGameMode(GameType.DEFAULT_MODE)
                pEntity.inventory.items.forEach { if (it.`is`(ModItems.ULTIMATE_BREAD.get())) it.count = 0 }
                pLevel.explode(pEntity, pEntity.x, pEntity.y, pEntity.z, 5.0f, Level.ExplosionInteraction.MOB)
            }

            val timeLeft = getTimeLeft(pStack)
            if (timeLeft <= 1) {
                pEntity.setGameMode(GameType.DEFAULT_MODE)
                pStack.count = 0
            } else {
                if (!pEntity.gameMode.isCreative) pEntity.setGameMode(GameType.CREATIVE)
                setTimeLeft(pStack, timeLeft - 1)
            }
        }
    }

    override fun getEntityLifespan(itemStack: ItemStack, level: Level): Int = 0

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)
    override fun displayInCreativeTab(
        pParameters: CreativeModeTab.ItemDisplayParameters,
        pOutput: CreativeModeTab.Output,
    ): Boolean {
        pOutput.accept(ItemStack(this).also {
            this.setTimeLeft(
                it,
                ModConfiguration.COMMON.ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS.get()
            )
        })
        pOutput.accept(ItemStack(this).also { this.setTimeLeft(it, Long.MAX_VALUE) })
        return false
    }

    companion object {
        val MAX_TIME_CONFIG = ModConfiguration.COMMON.ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS
        const val TIME_LEFT_NBT = "ticksLeft"
    }
}