package org.bread_experts_group.breadmod.experimental.camera_viewer.camera

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker

class CameraBlock : BreadModBlock(Properties.of().noOcclusion().noCollission()) {
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
		::CameraBlockRenderer

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.ENTITYBLOCK_ANIMATED

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		LerpTickerHandler.BLOCK_VOID to mutableMapOf(null to { _ ->
			LerpTickerHandler(0 to LerpTicker.LerpParams(clampMin = 0f, clampMax = 360f))
		}),
		CameraHandler.BLOCK_VOID to mutableMapOf(null to { _ -> CameraHandler() })
	)

	override val commonTickBM: BreadModTicker<Level> = { entity, _, _, _ ->
		val handler = entity.getLerpTicker<Int>()
		handler.tickCustom(0) {
			if (it.position >= 360f) it.position = 0f
			it.tick()
		}
	}
}