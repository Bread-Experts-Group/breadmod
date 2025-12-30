package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import java.math.BigDecimal

class DoughMachineBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofMenu(): (MenuType<*>, Int, Inventory, BreadModBlockEntity) -> BreadModMenu = ::DoughMachineMenu
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val itemStorage = ExtendedItemHandler.ofSlotsWithCapacity(4, 64)
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(
				null to { _ -> itemStorage },
				Direction.UP to { _ -> itemStorage.newProxy(0 to 0, 1 to 1) },
				Direction.DOWN to { _ -> itemStorage.newProxy(0 to 2, 1 to 3) },
			),
			this.setupHandlerPair(
				Capabilities.EnergyStorage.BLOCK,
				ExtendedEnergyHandler(BigDecimal(1000000), BigDecimal(5500)),
				*BreadModBlock.ALL_DIRECTIONS
			),
			this.setupHandlerPair(
				Capabilities.FluidHandler.BLOCK,
				ExtendedFluidHandler(ExtendedFluidHandler.Tank(BigDecimal.valueOf(10_000))),
				*BreadModBlock.ALL_DIRECTIONS
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
}