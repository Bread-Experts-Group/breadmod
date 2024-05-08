package breadmod.item

import breadmod.ModMain.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class BreadAmuletItem: Item(Properties()) {
    private var timer: Long = 200L
    override fun onInventoryTick(
        pStack: ItemStack,
        pLevel: Level,
        pPlayer: Player,
        slotIndex: Int,
        selectedIndex: Int
    ) {
        if(!pLevel.isClientSide) {
            val hungerLevel = pPlayer.foodData.foodLevel
            if(hungerLevel <= 20 && timer == 0L) {
                pPlayer.foodData.foodLevel+=2
                timer = 200L
            } else if(hungerLevel <= 19) timer--
        }
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(modTranslatable("item", "bread_amulet", "description").withStyle(ChatFormatting.BLUE))
    }
}