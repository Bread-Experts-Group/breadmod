package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.entity.block.DieselGeneratorRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.DieselGeneratorStateHandler
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_ONE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_THREE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_TWO
import java.math.BigDecimal
import java.util.Optional

class DieselGeneratorBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val state = DieselGeneratorStateHandler()
		val fluids = ExtendedFluidHandler(ExtendedFluidHandler.Tank(BigDecimal.valueOf(10_000)))
		val storage = { _: BreadModBlockEntity -> fluids }
		return mapOf(
			DieselGeneratorStateHandler.BLOCK_VOID to mapOf(Optional.empty<Any>() to { _ -> state }),
			Capabilities.FluidHandler.BLOCK to mapOf(
				null to storage,
				Direction.UP to storage,
				Direction.DOWN to storage,
				Direction.NORTH to storage,
				Direction.SOUTH to storage,
				Direction.EAST to storage,
				Direction.WEST to storage,
			)
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::DieselGeneratorRenderer

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(
			OPEN,
			HORIZONTAL_FACING,
			UPGRADE_ONE,
			UPGRADE_TWO,
			UPGRADE_THREE
		)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState = this.defaultBlockState()
		.setValue(OPEN, false)
		.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
		.setValue(UPGRADE_ONE, false)
		.setValue(UPGRADE_TWO, false)
		.setValue(UPGRADE_THREE, false)
}