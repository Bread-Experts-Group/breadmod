package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.entity.block.ModelBlockRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.ModelHandler

class ModelBlock : BreadModBlock(BlockBehaviour.Properties.of().noOcclusion()) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
		::ModelBlockRenderer

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.ENTITYBLOCK_ANIMATED

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection)

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		ModelHandler.BLOCK_VOID to mapOf(null to { _ -> ModelHandler() })
	)
}