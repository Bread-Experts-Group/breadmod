package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.FAIL
import net.minecraft.world.InteractionResult.sidedSuccess
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity
import org.bread_experts_group.breadmod.util.normalizeHitLoc
import org.bread_experts_group.breadmod.util.targetFaceSection
import java.util.stream.Stream

class MicrowaveBlock : AbstractTickingBlockWithBlockEntity(Properties.of()) {
	private companion object {
		val HORIZONTAL_FACING : DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
		val OPEN : BooleanProperty = BlockStateProperties.OPEN
		val SHAPE_NORTH : VoxelShape = Stream.of(
			box(1.0, 1.0, 3.0, 15.0, 9.0, 13.0),
			box(2.0, 2.0, 13.0, 14.0, 8.0, 14.0),
			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5),
			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5)
		).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_SOUTH : VoxelShape = Stream.of(
			box(1.0, 1.0, 3.0, 15.0, 9.0, 13.0),
			box(2.0, 2.0, 2.0, 14.0, 8.0, 3.0),
			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5),
			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_WEST : VoxelShape = Stream.of(
			box(3.0, 1.0, 1.0, 13.0, 9.0, 15.0),
			box(13.0, 2.0, 2.0, 14.0, 8.0, 14.0),
			box(3.5, 0.0, 1.5, 5.5, 1.0, 3.5),
			box(10.5, 0.0, 1.5, 12.5, 1.0, 3.5),
			box(10.5, 0.0, 12.5, 12.5, 1.0, 14.5),
			box(3.5, 0.0, 12.5, 5.5, 1.0, 14.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_EAST : VoxelShape = Stream.of(
			box(3.0, 1.0, 1.0, 13.0, 9.0, 15.0),
			box(2.0, 2.0, 2.0, 3.0, 8.0, 14.0),
			box(3.5, 0.0, 1.5, 5.5, 1.0, 3.5),
			box(10.5, 0.0, 1.5, 12.5, 1.0, 3.5),
			box(10.5, 0.0, 12.5, 12.5, 1.0, 14.5),
			box(3.5, 0.0, 12.5, 5.5, 1.0, 14.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

		val SHAPE_NORTH_OPEN : VoxelShape = Stream.of(
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
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_SOUTH_OPEN : VoxelShape = Stream.of(
			box(11.0, 1.0, 3.0, 15.0, 9.0, 13.0),
			box(1.0, 1.0, 3.0, 11.0, 2.0, 13.0),
			box(3.5, 2.0, 5.0, 9.5, 2.5, 11.0),
			box(1.0, 2.0, 3.0, 2.0, 8.0, 13.0),
			box(2.0, 2.0, 2.0, 11.0, 8.0, 4.0),
			box(11.0, 2.0, 2.0, 14.0, 8.0, 3.0),
			box(1.0, 8.0, 3.0, 11.0, 9.0, 13.0),
			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5),
			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_WEST_OPEN : VoxelShape = Stream.of(
			box(3.0, 1.0, 11.0, 13.0, 9.0, 15.0),
			box(3.0, 1.0, 1.0, 13.0, 2.0, 11.0),
			box(5.0, 2.1, 3.5, 11.0, 2.5, 9.5),
			box(3.0, 2.0, 1.0, 13.0, 8.0, 2.0),
			box(12.0, 2.0, 2.0, 14.0, 8.0, 11.0),
			box(13.0, 2.0, 11.0, 14.0, 8.0, 14.0),
			box(3.0, 8.0, 1.0, 13.0, 9.0, 11.0),
			box(3.5, 0.0, 1.5, 5.5, 1.0, 3.5),
			box(3.5, 0.0, 12.5, 5.5, 1.0, 14.5),
			box(10.5, 0.0, 12.5, 12.5, 1.0, 14.5),
			box(10.5, 0.0, 1.5, 12.5, 1.0, 3.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
		val SHAPE_EAST_OPEN : VoxelShape = Stream.of(
			box(3.0, 1.0, 1.0, 13.0, 9.0, 5.0),
			box(3.0, 1.0, 5.0, 13.0, 2.0, 15.0),
			box(5.0, 2.1, 6.5, 11.0, 2.5, 12.5),
			box(3.0, 2.0, 14.0, 13.0, 8.0, 15.0),
			box(2.0, 2.0, 5.0, 4.0, 8.0, 14.0),
			box(2.0, 2.0, 2.0, 3.0, 8.0, 5.0),
			box(3.0, 8.0, 5.0, 13.0, 9.0, 15.0),
			box(3.5, 0.0, 1.5, 5.5, 1.0, 3.5),
			box(3.5, 0.0, 12.5, 5.5, 1.0, 14.5),
			box(10.5, 0.0, 12.5, 12.5, 1.0, 14.5),
			box(10.5, 0.0, 1.5, 12.5, 1.0, 3.5)
		).reduce { v1 : VoxelShape, v2 : VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
	}

	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity = MicrowaveBlockEntity(pos, state)
	override fun useWithoutItem(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hitResult : BlockHitResult
	) : InteractionResult {
		val direction = hitResult.direction ?: return FAIL
		val hitLoc = hitResult.location
		val hitX = normalizeHitLoc(hitLoc.x, pos.x)
		val hitY = normalizeHitLoc(hitLoc.y, pos.y)
		val hitZ = normalizeHitLoc(hitLoc.z, pos.z)
		when (direction) {
			NORTH -> {
				this.buttonPress(hitX, hitY, 0.23, 0.27, level, pos, NORTH)
			}
			EAST  -> {
				this.buttonPress(hitZ, hitY, 0.23, 0.27, level, pos, EAST)
			}
			SOUTH -> {
				this.buttonPress(hitX, hitY, 0.73, 0.77, level, pos, SOUTH)
			}
			WEST  -> {
				this.buttonPress(hitZ, hitY, 0.73, 0.77, level, pos, WEST)
			}
			else  -> {}
		}
		return sidedSuccess(level.isClientSide)
	}

	private fun sound(level : Level, pos : BlockPos, pitch : Float) =
		level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), BLOCKS, 1f, pitch)

	private fun buttonPress(
		targetX : Double,
		targetY : Double,
		startMinX : Double,
		startMaxX : Double,
		level : Level,
		pos : BlockPos,
		facing : Direction
	) : Int {
		repeat(4) { yIndex ->
			val yMul = (yIndex / 100.0) * 5

			repeat(3) { xIndex ->
				val xMul = (xIndex / 100.0) * 6
				val pitchMul = (xIndex / 100.0f) * 2
				if (targetFaceSection(
						targetX, targetY,
						if (facing == NORTH || facing == EAST) startMinX - xMul else startMinX + xMul,
						0.3 - yMul,
						if (facing == NORTH || facing == EAST) startMaxX - xMul else startMaxX + xMul,
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

	override fun useItemOn(
		stack : ItemStack,
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hand : InteractionHand,
		hitResult : BlockHitResult
	) : ItemInteractionResult {
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
	}

	override fun getLightEmission(state : BlockState, level : BlockGetter, pos : BlockPos) : Int {
		return super.getLightEmission(state, level, pos)
	}

	override fun createBlockStateDefinition(builder : Builder<Block, BlockState>) {
		builder.add(Companion.HORIZONTAL_FACING, Companion.OPEN)
	}

	override fun getStateForPlacement(context : BlockPlaceContext) : BlockState =
		this.defaultBlockState()
			.setValue(Companion.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(Companion.OPEN, false)

	override fun getShape(
		state : BlockState,
		level : BlockGetter,
		pos : BlockPos,
		context : CollisionContext
	) : VoxelShape {
		val direction = state.getValue(Companion.HORIZONTAL_FACING) ?: return Shapes.block()
		return if (state.getValue(Companion.OPEN)) {
			when (direction) {
				NORTH -> Companion.SHAPE_NORTH_OPEN
				SOUTH -> Companion.SHAPE_SOUTH_OPEN
				WEST  -> Companion.SHAPE_WEST_OPEN
				EAST  -> Companion.SHAPE_EAST_OPEN
				UP, DOWN -> Shapes.block()
			}
		} else when (direction) {
			NORTH -> Companion.SHAPE_NORTH
			SOUTH -> Companion.SHAPE_SOUTH
			WEST  -> Companion.SHAPE_WEST
			EAST  -> Companion.SHAPE_EAST
			UP, DOWN -> Shapes.block()
		}
	}
}