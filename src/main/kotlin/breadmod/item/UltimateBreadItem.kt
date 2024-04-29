package breadmod.item

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

class UltimateBreadItem: Item(Properties().stacksTo(1).fireResistant()) {
    override fun isEdible(): Boolean = true
    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        if(pLivingEntity is ServerPlayer) {
            // get ultimate bread capability
            pLivingEntity.setGameMode(GameType.CREATIVE)
            //scheduleServerEvent(600 * 20) { pLivingEntity.setGameMode(GameType.DEFAULT_MODE) }
        }
        return ItemStack.EMPTY
    }

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pEntity is ServerPlayer && pEntity.gameMode.isCreative) {

        }
    }
}