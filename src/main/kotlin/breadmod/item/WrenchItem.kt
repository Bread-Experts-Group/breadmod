package breadmod.item

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.minecraftforge.common.capabilities.ForgeCapabilities

class WrenchItem: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        val level = context.level
        if(level.isClientSide) return InteractionResult.PASS
        level.getBlockEntity(context.clickedPos)?.let {
            if(it is AbstractMachineBlockEntity<*>) {
                it.capabilityHolder.capabilities[ForgeCapabilities.ENERGY]?.second?.removeIf { it != null }
                return InteractionResult.CONSUME
            }
        }
        return InteractionResult.PASS
    }
}