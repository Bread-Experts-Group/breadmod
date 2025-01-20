package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.util.handlers.ExtendedItemStackHandler

class ItemInWorldBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<ItemInWorldBlockEntity>(
	ModBlockEntityTypes.ITEM_IN_WORLD.get(),
	pos,
	state
), ItemBearingBlockEntity {
	override val itemHandler: ExtendedItemStackHandler = ExtendedItemStackHandler(4)
}