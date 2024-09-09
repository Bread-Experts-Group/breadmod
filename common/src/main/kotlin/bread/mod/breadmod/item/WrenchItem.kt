package bread.mod.breadmod.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.state.properties.BlockStateProperties


// todo port over machines
/**
 * Wrench item, used for configuring the sides of machines, or toggling the [BlockStateProperties.ENABLED] state of machines.
 *
 * @see [AbstractMachineBlockEntity]
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class WrenchItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    /**
     * Called when the player uses the item on a block; if this is on a [AbstractMachineBlockEntity], the wrench item will consume
     * the current use.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
//    override fun useOn(context: UseOnContext): InteractionResult? {
//        val level = context.level
//        if (level.isClientSide) return InteractionResult.PASS
//        level.getBlockEntity(context.clickedPos)?.let {
//            if (it is AbstractMachineBlockEntity<*>) {
//                it.capabilityHolder.getSidedness(ForgeCapabilities.ENERGY)
//
//
//                return InteractionResult.CONSUME
//            }
//        }
//        return InteractionResult.PASS
//    }
}