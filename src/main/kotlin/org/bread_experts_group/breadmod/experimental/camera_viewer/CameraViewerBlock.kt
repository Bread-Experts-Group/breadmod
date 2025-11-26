package org.bread_experts_group.breadmod.experimental.camera_viewer

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.util.`is`

// todo system like computercraft's monitors to join together multiple viewer blocks,
//  use BlockStates to mark the block as master or not and pass it into shouldCreateEntity
//  use getStateForPlacement to scan for neighbouring monitors
class CameraViewerBlock : BreadModBlock(BlockBehaviour.Properties.of()) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
		::CameraViewerBlockRenderer

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		CameraViewerHandler.BLOCK_VOID to mapOf(null to { _ -> CameraViewerHandler() })
	)

	override val commonTickBM: BreadModTicker<Level> = ticker@{ entity, level, _, pos ->
		val handler = entity.getCapability(CameraViewerHandler.BLOCK_VOID)
		if (handler.boundPos == BlockPos.ZERO) return@ticker
		val cameraExists = level.getBlockState(handler.boundPos).`is`(ModBlocks.CAMERA_BLOCK)
		if (!cameraExists) {
			if (CameraTexture.textures[pos] != null) CameraTexture.textures.remove(pos)?.close()
			handler.boundPos = BlockPos.ZERO
			entity.setChanged()
		}
	}
}