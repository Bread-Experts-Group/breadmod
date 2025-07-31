package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.sidedSuccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.entity.block.MicrowaveRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.util.combine
import org.bread_experts_group.breadmod.util.normalizedHitPos
import org.bread_experts_group.breadmod.util.rotate
import org.bread_experts_group.breadmod.util.targetFaceSection
import java.math.BigDecimal
import java.util.Optional
import java.util.stream.Stream

class MicrowaveBlock : BreadModBlock(Properties.of()) {
	private companion object {
		val SHAPE_NORTH: VoxelShape = Stream.of(
			box(1.0, 1.0, 3.0, 15.0, 9.0, 13.0),
			box(2.0, 2.0, 13.0, 14.0, 8.0, 14.0),
			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5),
			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5)
		).combine()
		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.COUNTERCLOCKWISE_90)
		val SHAPE_NORTH_OPEN: VoxelShape = Stream.of(
			box(1.0, 1.0, 3.0, 5.0, 9.0, 13.0),
			box(5.0, 1.0, 3.0, 15.0, 2.0, 13.0),
			box(6.5, 2.0, 5.0, 12.5, 2.5, 11.0),
			box(14.0, 2.0, 3.0, 15.0, 8.0, 13.0),
			box(5.0, 2.0, 12.0, 14.0, 8.0, 14.0),
			box(2.0, 2.0, 13.0, 5.0, 8.0, 14.0),
			box(5.0, 8.0, 3.0, 15.0, 9.0, 13.0),
			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5),
			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5)
		).combine()
		val SHAPE_SOUTH_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_EAST_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_WEST_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(Rotation.COUNTERCLOCKWISE_90)
	}

	override fun ofCapabilities(): CapabilityMap {
		val storage = ExtendedItemHandler(ExtendedItemHandler.Slot(BigDecimal.ONE))
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(Optional.empty<Any>() to { _, _ -> storage })
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::MicrowaveRenderer

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		hitResult.direction ?: return InteractionResult.FAIL
		normalizedHitPos(hitResult.location, pos)
		val entity = level.getBlockEntity(pos) as BreadModBlockEntity
		val storage = entity.getCapability(Capabilities.ItemHandler.BLOCK) as ExtendedItemHandler
//		val number = when (direction) {
//			Direction.NORTH -> this.buttonPress(normalizedPos.x, normalizedPos.y, 0.23, 0.27, level, pos, NORTH)
//			Direction.EAST -> this.buttonPress(normalizedPos.z, normalizedPos.y, 0.23, 0.27, level, pos, EAST)
//			Direction.SOUTH -> this.buttonPress(normalizedPos.x, normalizedPos.y, 0.73, 0.77, level, pos, SOUTH)
//			Direction.WEST -> this.buttonPress(normalizedPos.z, normalizedPos.y, 0.73, 0.77, level, pos, WEST)
//			else -> -1
//		}
		if (storage.getStackInSlot(0).isEmpty) {
			val stack = player.getItemInHand(player.usedItemHand)
			val inserted = storage.insertItem(
				0,
				player.getItemInHand(player.usedItemHand),
				false
			)
			stack.count = inserted.count
		}
		return sidedSuccess(level.isClientSide)
	}

	private fun sound(level: Level, pos: BlockPos, pitch: Float): Unit = level.playSound(
		null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS,
		1f, pitch
	)

	private fun buttonPress(
		targetX: Double,
		targetY: Double,
		startMinX: Double,
		startMaxX: Double,
		level: Level,
		pos: BlockPos,
		facing: Direction
	): Int {
		repeat(4) { yIndex ->
			val yMul = (yIndex / 100.0) * 5

			repeat(3) { xIndex ->
				val xMul = (xIndex / 100.0) * 6
				val pitchMul = (xIndex / 100.0f) * 2
				if (
					targetFaceSection(
						targetX, targetY,
						if (facing == Direction.NORTH || facing == Direction.EAST) startMinX - xMul else startMinX + xMul,
						0.3 - yMul,
						if (facing == Direction.NORTH || facing == Direction.EAST) startMaxX - xMul else startMaxX + xMul,
						0.33 - yMul
					)
				) {
					this.sound(level, pos, 0.5f + pitchMul + yMul.toFloat())
					return (xIndex + 1) + (yIndex * 3)
				}
			}
		}
		return 0
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, OPEN)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(OPEN, false)

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		val direction = state.getValue(HORIZONTAL_FACING) ?: return Shapes.block()
		return if (state.getValue(OPEN)) {
			when (direction) {
				Direction.NORTH -> Companion.SHAPE_NORTH_OPEN
				Direction.SOUTH -> Companion.SHAPE_SOUTH_OPEN
				Direction.WEST -> Companion.SHAPE_WEST_OPEN
				Direction.EAST -> Companion.SHAPE_EAST_OPEN
				Direction.UP, Direction.DOWN -> Shapes.block()
			}
		} else when (direction) {
			Direction.NORTH -> Companion.SHAPE_NORTH
			Direction.SOUTH -> Companion.SHAPE_SOUTH
			Direction.WEST -> Companion.SHAPE_WEST
			Direction.EAST -> Companion.SHAPE_EAST
			Direction.UP, Direction.DOWN -> Shapes.block()
		}
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
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}