package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
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
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_ONE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_THREE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.UPGRADE_TWO
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.DieselGeneratorStateHandler
import java.math.BigDecimal

class DieselGeneratorBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		this.setupHandlerPair(DieselGeneratorStateHandler.BLOCK_VOID, DieselGeneratorStateHandler()),
		this.setupHandlerPair(
			Capabilities.FluidHandler.BLOCK,
			ExtendedFluidHandler(ExtendedFluidHandler.Tank(BigDecimal.valueOf(10_000))),
			*BreadModBlock.ALL_DIRECTIONS
		)
	)

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
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