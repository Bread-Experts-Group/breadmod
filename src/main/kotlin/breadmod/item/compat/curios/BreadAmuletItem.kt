package breadmod.item.compat.curios

import breadmod.ModMain.modTranslatable
import breadmod.registry.ModConfiguration.COMMON
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
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

class BreadAmuletItem: Item(Properties().stacksTo(1)) {
    private var timer: Int = COMMON.BREAD_AMULET_FEED_TIME_TICKS.get()
    private var alreadyRun: Int = 0
    private fun playerFood(pPlayer: ServerPlayer) {
        if(alreadyRun != pPlayer.tickCount) {
            val hungerLevel = pPlayer.foodData.foodLevel
            if(hungerLevel <= 20 && timer <= 0) {
                pPlayer.foodData.foodLevel += COMMON.BREAD_AMULET_FEED_AMOUNT.get()
                timer = COMMON.BREAD_AMULET_FEED_TIME_TICKS.get()
            } else if(hungerLevel <= 19) timer--
            alreadyRun = pPlayer.tickCount
        }
    }

    // TODO fix item effects stacking with multiple of the same item in a player's inventory

    override fun onInventoryTick(pStack: ItemStack, pLevel: Level, pPlayer: Player, slotIndex: Int, selectedIndex: Int): Unit =
        if(pPlayer is ServerPlayer) playerFood(pPlayer) else {}

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(modTranslatable("item", "bread_amulet", "description").withStyle(ChatFormatting.GOLD))
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