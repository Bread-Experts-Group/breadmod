package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import java.math.BigDecimal
import java.util.Optional

class DoughMachineBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofMenu(): (MenuType<*>, Int, Inventory, BreadModBlockEntity) -> BreadModMenu = ::DoughMachineMenu
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val itemStorage = ExtendedItemHandler(
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal())
		)
		val energy = ExtendedEnergyHandler(BigDecimal(10000))
		val energyStorage = { _: BreadModBlockEntity -> energy }
		val fluid = ExtendedFluidHandler(ExtendedFluidHandler.Tank(BigDecimal.valueOf(10_000)))
		val fluidStorage = { _: BreadModBlockEntity -> fluid }
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(Optional.empty<Any>() to { _ -> itemStorage }),
			Capabilities.EnergyStorage.BLOCK to mapOf(
				null to energyStorage,
				Direction.UP to energyStorage,
				Direction.DOWN to energyStorage,
				Direction.NORTH to energyStorage,
				Direction.SOUTH to energyStorage,
				Direction.EAST to energyStorage,
				Direction.WEST to energyStorage,
			),
			Capabilities.FluidHandler.BLOCK to mapOf(
				null to fluidStorage,
				Direction.UP to fluidStorage,
				Direction.DOWN to fluidStorage,
				Direction.NORTH to fluidStorage,
				Direction.SOUTH to fluidStorage,
				Direction.EAST to fluidStorage,
				Direction.WEST to fluidStorage,
			)
		)
	}

	override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean =
		!player.isCreative

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.POWERED, false)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
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
}