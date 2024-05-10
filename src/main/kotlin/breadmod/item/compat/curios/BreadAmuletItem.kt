package breadmod.item.compat.curios

import breadmod.ModMain.modTranslatable
import breadmod.registry.ModConfiguration.COMMON
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.ModList
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurio
import java.text.DecimalFormat

class BreadAmuletItem: Item(Properties().stacksTo(1)) {
    companion object {
        private val feedTime = COMMON.BREAD_AMULET_FEED_TIME_TICKS
        private val feedAmt = COMMON.BREAD_AMULET_FEED_AMOUNT
        private val feedStacks = COMMON.BREAD_AMULET_STACKS

        data class PlayerData(var timeLeft: Int, var lastExec: Int)
        val timers = mutableMapOf<String,PlayerData>()
    }

    private fun playerFood(pPlayer: ServerPlayer) {
        val timer = timers.getOrPut(pPlayer.stringUUID) { PlayerData(feedTime.get(), 0) }
        if(feedStacks.get() || timer.lastExec != pPlayer.tickCount) {
            val hungerLevel = pPlayer.foodData.foodLevel
            if(hungerLevel <= 20 && timer.timeLeft <= 0) {
                pPlayer.foodData.foodLevel += feedAmt.get()
                timer.timeLeft = feedTime.get()
            } else if(hungerLevel <= 19) timer.timeLeft--
            timer.lastExec = pPlayer.tickCount
        }
    }

    override fun onInventoryTick(pStack: ItemStack, pLevel: Level, pPlayer: Player, slotIndex: Int, selectedIndex: Int): Unit =
        if(pPlayer is ServerPlayer) playerFood(pPlayer) else {}

    private val decimalFormat = DecimalFormat("0.#")
    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        val bars = feedAmt.get().toDouble() / 2
        val secDelay = feedTime.get().toDouble() / 20

        pTooltipComponents.add(modTranslatable(
            "item",
            "bread_amulet", "description",
            args = listOf(
                if(bars == 1.0) "a bar" else "${decimalFormat.format(bars)} bars",
                if(secDelay == 1.0) "second" else "${decimalFormat.format(secDelay)} seconds"
            )
        ).append(
            if(feedStacks.get()) Component.literal(" ").append(modTranslatable("item", "bread_amulet", "stacks"))
            else Component.empty()
        ).withStyle(ChatFormatting.GOLD))
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? =
        if(ModList.get().isLoaded("curios")) {
            CuriosApi.createCurioProvider(object : ICurio {
                override fun getStack(): ItemStack {
                    return stack
                }

                override fun curioTick(slotContext: SlotContext) = slotContext.entity.let {
                    if(it !is ServerPlayer) return
                    playerFood(it)
                }
            })
        } else null
}