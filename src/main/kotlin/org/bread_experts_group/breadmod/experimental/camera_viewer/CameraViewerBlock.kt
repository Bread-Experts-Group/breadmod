package org.bread_experts_group.breadmod.experimental.camera_viewer

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.util.directionalTargetFaceSection
import org.bread_experts_group.breadmod.util.`is`
import org.bread_experts_group.breadmod.util.normalizedHitPos

// todo system like computercraft's monitors to join together multiple viewer blocks,
//  use BlockStates to mark the block as master or not and pass it into shouldCreateEntity
//  use getStateForPlacement to scan for neighbouring monitors
class CameraViewerBlock : BreadModBlock(BlockBehaviour.Properties.of()) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
		::CameraViewerBlockRenderer

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.POWERED, false)

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		CameraViewerHandler.BLOCK_VOID to mapOf(null to { _ -> CameraViewerHandler() })
	)

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val direction = hitResult.direction ?: return InteractionResult.FAIL
		val normalizedPos = normalizedHitPos(hitResult.location, pos)

		if (
			directionalTargetFaceSection(
				direction, normalizedPos,
				0.12, 0.18, 0.82, 0.88,
				0.00, 0.06
			)
		) {
			val powered = state.getValue(BlockStateProperties.POWERED)
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, !powered))
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		return InteractionResult.PASS
	}

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