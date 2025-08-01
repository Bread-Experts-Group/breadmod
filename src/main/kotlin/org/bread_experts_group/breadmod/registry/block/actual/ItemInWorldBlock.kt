package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.client.render.entity.block.ItemInWorldRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.SlotQueueHandler
import java.util.Optional

class ItemInWorldBlock : BreadModBlock(
	Properties.of()
		.pushReaction(PushReaction.DESTROY)
) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap {
		val inventory = SlotQueueHandler()
		return mapOf(
			SlotQueueHandler.BLOCK_VOID to mapOf(Optional.empty<Any>() to { _, _ -> inventory })
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::ItemInWorldRenderer

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState = this.defaultBlockState()
		.setValue(BlockStateProperties.FACING, context.nearestLookingDirection.opposite)

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		val level = (level as? Level) ?: return Shapes.empty()
		val items = level.getCapability(
			SlotQueueHandler.BLOCK_VOID,
			pos, state, level.getBlockEntity(pos)
		) ?: return Shapes.empty()
		return items.shape
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE

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
			val itemHandler = entity.getCapability(SlotQueueHandler.BLOCK_VOID)
			itemHandler.dropContents(pos, level)
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}