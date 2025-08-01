package org.bread_experts_group.breadmod.registry.block.actual.machine

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import java.util.Optional

class DoughMachineBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofMenu(): (MenuType<*>, Int, Inventory, BreadModBlockEntity) -> BreadModMenu = ::DoughMachineMenu
	override fun ofCapabilities(): CapabilityMap {
		val storage = ExtendedItemHandler(
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal())
		)
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(Optional.empty<Any>() to { _, _ -> storage })
		)
	}

	override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean =
		!player.isCreative

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(POWERED, false)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, POWERED)
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (!level.isClientSide) {
			val entity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return InteractionResult.FAIL
			player.openMenu(entity, pos)
		}
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = level.getBlockEntity(pos) as? BreadModBlockEntity
				?: return super.onRemove(state, level, pos, newState, movedByPiston)
			val itemHandler = entity.getCapability(Capabilities.ItemHandler.BLOCK) as? ExtendedItemHandler
				?: return super.onRemove(state, level, pos, newState, movedByPiston)
			itemHandler.dropContents(pos, level)
		}
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}