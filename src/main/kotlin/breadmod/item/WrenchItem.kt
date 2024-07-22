package breadmod.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level

class WrenchItem: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }
}