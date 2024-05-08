package breadmod.item.compat.curios

import breadmod.ModMain.modTranslatable
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

class BreadAmuletItem: Item(Properties()) {
    private var timer: Long = 200L
    private fun playerFood(pPlayer: Player) {
        val hungerLevel = pPlayer.foodData.foodLevel
        if(hungerLevel <= 20 && timer == 0L) {
            pPlayer.foodData.foodLevel+=2
            timer = 200L
        } else if(hungerLevel <= 19) timer--
    }

    override fun onInventoryTick(pStack: ItemStack, pLevel: Level, pPlayer: Player, slotIndex: Int, selectedIndex: Int) = playerFood(pPlayer)

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