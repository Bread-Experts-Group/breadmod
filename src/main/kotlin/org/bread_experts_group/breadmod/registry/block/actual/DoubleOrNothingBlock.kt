package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
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
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.LOWER
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.MIDDLE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.UPPER
import org.bread_experts_group.breadmod.util.directionalTargetFaceSection
import org.bread_experts_group.breadmod.util.join
import org.bread_experts_group.breadmod.util.normalizedHitPos
import java.util.stream.Stream

class DoubleOrNothingBlock : BaseEntityBlock(Properties.of()) {
	companion object {
		val TRIPLE_HALF: EnumProperty<TripleBlockHalf> = ModBlockStateProperties.TRIPLE_BLOCK_HALF
		val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

		// todo the rest of the VoxelShapes
		// shapes lower
		val SHAPE_LOWER_NORTH: VoxelShape = Stream.of(
			box(1.0, 7.0, 9.0, 15.0, 16.0, 16.0),
			box(15.0, 7.0, 8.0, 16.0, 16.0, 16.0),
			box(0.0, 7.0, 8.0, 1.0, 16.0, 16.0),
			box(10.0, 7.0, 4.6, 13.0, 9.4, 7.6),
			box(3.0, 7.0, 4.6, 6.0, 9.4, 7.6),
			box(0.0, 0.0, 3.0, 16.0, 7.0, 16.0),
			box(1.0, 1.0, 2.8, 15.0, 6.0, 3.0),
			box(15.0, 7.0, 3.0, 16.0, 8.0, 4.0),
			box(15.0, 7.0, 4.0, 16.0, 9.0, 5.0),
			box(15.0, 7.0, 5.0, 16.0, 10.0, 6.0),
			box(15.0, 7.0, 6.0, 16.0, 11.0, 7.0),
			box(15.0, 7.0, 7.0, 16.0, 12.0, 8.0),
			box(0.0, 7.0, 7.0, 1.0, 12.0, 8.0),
			box(0.0, 7.0, 6.0, 1.0, 11.0, 7.0),
			box(0.0, 7.0, 5.0, 1.0, 10.0, 6.0),
			box(0.0, 7.0, 3.0, 1.0, 8.0, 4.0),
			box(0.0, 7.0, 4.0, 1.0, 9.0, 5.0)
		).reduce(::join).get()
		val SHAPE_LOWER_SOUTH: VoxelShape = Stream.of(
			box(1.0, 7.0, 0.0, 15.0, 16.0, 7.0),
			box(0.0, 7.0, 0.0, 1.0, 16.0, 8.0),
			box(15.0, 7.0, 0.0, 16.0, 16.0, 8.0),
			box(10.0, 7.0, 8.6, 13.0, 9.4, 11.6),
			box(3.0, 7.0, 8.6, 6.0, 9.4, 11.6),
			box(0.0, 0.0, 0.0, 16.0, 7.0, 13.0),
			box(1.0, 1.0, 13.0, 15.0, 6.0, 13.2),
			box(0.0, 7.0, 12.0, 1.0, 8.0, 13.0),
			box(0.0, 7.0, 11.0, 1.0, 9.0, 12.0),
			box(0.0, 7.0, 10.0, 1.0, 10.0, 11.0),
			box(0.0, 7.0, 9.0, 1.0, 11.0, 10.0),
			box(0.0, 7.0, 8.0, 1.0, 12.0, 9.0),
			box(15.0, 7.0, 8.0, 16.0, 12.0, 9.0),
			box(15.0, 7.0, 9.0, 16.0, 11.0, 10.0),
			box(15.0, 7.0, 10.0, 16.0, 10.0, 11.0),
			box(15.0, 7.0, 12.0, 16.0, 8.0, 13.0),
			box(15.0, 7.0, 11.0, 16.0, 9.0, 12.0)
		).reduce(::join).get()
		val SHAPE_LOWER_EAST: VoxelShape = Stream.of(
			box(0.0, 7.0, 1.0, 7.0, 16.0, 15.0),
			box(0.0, 7.0, 15.0, 8.0, 16.0, 16.0),
			box(0.0, 7.0, 0.0, 8.0, 16.0, 1.0),
			box(8.4, 7.0, 10.0, 11.4, 9.4, 13.0),
			box(8.4, 7.0, 3.0, 11.4, 9.4, 6.0),
			box(0.0, 0.0, 0.0, 13.0, 7.0, 16.0),
			box(13.0, 1.0, 1.0, 13.2, 6.0, 15.0),
			box(12.0, 7.0, 15.0, 13.0, 8.0, 16.0),
			box(11.0, 7.0, 15.0, 12.0, 9.0, 16.0),
			box(10.0, 7.0, 15.0, 11.0, 10.0, 16.0),
			box(9.0, 7.0, 15.0, 10.0, 11.0, 16.0),
			box(8.0, 7.0, 15.0, 9.0, 12.0, 16.0),
			box(8.0, 7.0, 0.0, 9.0, 12.0, 1.0),
			box(9.0, 7.0, 0.0, 10.0, 11.0, 1.0),
			box(10.0, 7.0, 0.0, 11.0, 10.0, 1.0),
			box(12.0, 7.0, 0.0, 13.0, 8.0, 1.0),
			box(11.0, 7.0, 0.0, 12.0, 9.0, 1.0)
		).reduce(::join).get()
		val SHAPE_LOWER_WEST: VoxelShape = Stream.of(
			box(9.0, 7.0, 1.0, 16.0, 16.0, 15.0),
			box(8.0, 7.0, 0.0, 16.0, 16.0, 1.0),
			box(8.0, 7.0, 15.0, 16.0, 16.0, 16.0),
			box(4.6, 7.0, 3.0, 7.6, 9.4, 6.0),
			box(4.6, 7.0, 10.0, 7.6, 9.4, 13.0),
			box(3.0, 0.0, 0.0, 16.0, 7.0, 16.0),
			box(2.8, 1.0, 1.0, 3.0, 6.0, 15.0),
			box(3.0, 7.0, 0.0, 4.0, 8.0, 1.0),
			box(4.0, 7.0, 0.0, 5.0, 9.0, 1.0),
			box(5.0, 7.0, 0.0, 6.0, 10.0, 1.0),
			box(6.0, 7.0, 0.0, 7.0, 11.0, 1.0),
			box(7.0, 7.0, 0.0, 8.0, 12.0, 1.0),
			box(7.0, 7.0, 15.0, 8.0, 12.0, 16.0),
			box(6.0, 7.0, 15.0, 7.0, 11.0, 16.0),
			box(5.0, 7.0, 15.0, 6.0, 10.0, 16.0),
			box(3.0, 7.0, 15.0, 4.0, 8.0, 16.0),
			box(4.0, 7.0, 15.0, 5.0, 9.0, 16.0)
		).reduce(::join).get()
		val SHAPE_MIDDLE_NORTH: VoxelShape = Stream.of(
			box(15.0, 0.0, 8.0, 16.0, 16.0, 16.0),
			box(1.0, 0.0, 9.0, 15.0, 16.0, 16.0),
			box(0.0, 0.0, 8.0, 1.0, 16.0, 16.0)
		).reduce(::join).get()
		val SHAPE_MIDDLE_SOUTH: VoxelShape = Stream.of(
			box(15.0, 0.0, 0.0, 16.0, 16.0, 8.0),
			box(1.0, 0.0, 0.0, 15.0, 16.0, 7.0),
			box(0.0, 0.0, 0.0, 1.0, 16.0, 8.0)
		).reduce(::join).get()
		val SHAPE_MIDDLE_EAST: VoxelShape = Stream.of(
			box(0.0, 0.0, 15.0, 8.0, 16.0, 16.0),
			box(0.0, 0.0, 1.0, 7.0, 16.0, 15.0),
			box(0.0, 0.0, 0.0, 8.0, 16.0, 1.0)
		).reduce(::join).get()
		val SHAPE_MIDDLE_WEST: VoxelShape = Stream.of(
			box(8.0, 0.0, 15.0, 16.0, 16.0, 16.0),
			box(9.0, 0.0, 1.0, 16.0, 16.0, 15.0),
			box(8.0, 0.0, 0.0, 16.0, 16.0, 1.0)
		).reduce(::join).get()

		// shapes upper
		val SHAPE_UPPER_NORTH: VoxelShape = Stream.of(
			box(1.0, 0.0, 9.0, 15.0, 6.0, 16.0),
			box(0.0, 0.0, 8.0, 1.0, 7.0, 16.0),
			box(1.0, 6.0, 8.0, 15.0, 7.0, 16.0),
			box(0.0, 9.0, 11.0, 16.0, 16.0, 13.0),
			box(2.0, 7.0, 11.5, 3.0, 9.0, 12.5),
			box(13.0, 7.0, 11.5, 14.0, 9.0, 12.5),
			box(15.0, 0.0, 8.0, 16.0, 7.0, 16.0),
			box(1.0, 10.0, 10.8, 15.0, 15.0, 11.0)
		).reduce(::join).get()
		val SHAPE_UPPER_SOUTH: VoxelShape = Stream.of(
			box(1.0, 0.0, 0.0, 15.0, 6.0, 7.0),
			box(0.0, 0.0, 0.0, 1.0, 7.0, 8.0),
			box(1.0, 6.0, 0.0, 15.0, 7.0, 8.0),
			box(0.0, 9.0, 3.0, 16.0, 16.0, 5.0),
			box(2.0, 7.0, 3.5, 3.0, 9.0, 4.5),
			box(13.0, 7.0, 3.5, 14.0, 9.0, 4.5),
			box(15.0, 0.0, 0.0, 16.0, 7.0, 8.0),
			box(1.0, 10.0, 5.0, 15.0, 15.0, 5.2)
		).reduce(::join).get()
		val SHAPE_UPPER_EAST: VoxelShape = Stream.of(
			box(0.0, 0.0, 1.0, 7.0, 6.0, 15.0),
			box(0.0, 0.0, 15.0, 8.0, 7.0, 16.0),
			box(0.0, 6.0, 1.0, 8.0, 7.0, 15.0),
			box(3.0, 9.0, 0.0, 5.0, 16.0, 16.0),
			box(3.5, 7.0, 2.0, 4.5, 9.0, 3.0),
			box(3.5, 7.0, 13.0, 4.5, 9.0, 14.0),
			box(0.0, 0.0, 0.0, 8.0, 7.0, 1.0),
			box(5.0, 10.0, 1.0, 5.2, 15.0, 15.0)
		).reduce(::join).get()
		val SHAPE_UPPER_WEST: VoxelShape = Stream.of(
			box(9.0, 0.0, 1.0, 16.0, 6.0, 15.0),
			box(8.0, 0.0, 15.0, 16.0, 7.0, 16.0),
			box(8.0, 6.0, 1.0, 16.0, 7.0, 15.0),
			box(11.0, 9.0, 0.0, 13.0, 16.0, 16.0),
			box(11.5, 7.0, 2.0, 12.5, 9.0, 3.0),
			box(11.5, 7.0, 13.0, 12.5, 9.0, 14.0),
			box(8.0, 0.0, 0.0, 16.0, 7.0, 1.0),
			box(10.8, 10.0, 1.0, 11.0, 15.0, 15.0)
		).reduce(::join).get()
	}

	override fun codec(): MapCodec<out BaseEntityBlock> = BlockBehaviour.simpleCodec { this }

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(Companion.TRIPLE_HALF, Companion.FACING)
	}

	override fun playerDestroy(
		level: Level,
		player: Player,
		pos: BlockPos,
		state: BlockState,
		blockEntity: BlockEntity?,
		tool: ItemStack
	) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool)
	}

	override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
		return super.playerWillDestroy(level, pos, state, player)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
		val pos = context.clickedPos
		val level = context.level
		return if (
			pos.y < level.maxBuildHeight - 2 &&
			level.getBlockState(pos.above()).canBeReplaced(context) &&
			level.getBlockState(pos.above().above()).canBeReplaced(context)
		) {
			return this.defaultBlockState()
				.setValue(Companion.FACING, context.horizontalDirection.opposite)
				.setValue(Companion.TRIPLE_HALF, LOWER)
		} else null
	}

	override fun getRenderShape(state: BlockState): RenderShape = INVISIBLE

	override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		level.setBlockAndUpdate(pos.above(), state.setValue(Companion.TRIPLE_HALF, MIDDLE))
		level.setBlockAndUpdate(pos.above().above(), state.setValue(Companion.TRIPLE_HALF, UPPER))
	}

	override fun updateShape(
		state: BlockState,
		facing: Direction,
		neighborState: BlockState,
		level: LevelAccessor,
		pos: BlockPos,
		neighborPos: BlockPos
	): BlockState {
		val half = state.getValue(Companion.TRIPLE_HALF)
		// todo only breaks all three blocks when the middle or lower block is broken, upper block doesn't break the other two.
		//  look into DoorBlock and try to make a three block tall variant that works properly..
		return if (facing.axis != Axis.Y || (half == LOWER != (facing == UP))) {
			if (half == LOWER && facing == DOWN) {
				Blocks.AIR.defaultBlockState()
			} else super.updateShape(state, facing, neighborState, level, pos, neighborPos)
		} else {
			if (neighborState.block is DoubleOrNothingBlock && neighborState.getValue(Companion.TRIPLE_HALF) != half)
				neighborState.setValue(Companion.TRIPLE_HALF, half) else Blocks.AIR.defaultBlockState()
		}
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		when (state.getValue(Companion.FACING)) {
			NORTH -> {
				when (state.getValue(Companion.TRIPLE_HALF)) {
					UPPER  -> Companion.SHAPE_UPPER_NORTH
					MIDDLE -> Companion.SHAPE_MIDDLE_NORTH
					LOWER  -> Companion.SHAPE_LOWER_NORTH
					else   -> Shapes.block()
				}
			}
			SOUTH -> {
				when (state.getValue(Companion.TRIPLE_HALF)) {
					UPPER  -> Companion.SHAPE_UPPER_SOUTH
					MIDDLE -> Companion.SHAPE_MIDDLE_SOUTH
					LOWER  -> Companion.SHAPE_LOWER_SOUTH
					else   -> Shapes.block()
				}
			}
			WEST  -> {
				when (state.getValue(Companion.TRIPLE_HALF)) {
					UPPER  -> Companion.SHAPE_UPPER_WEST
					MIDDLE -> Companion.SHAPE_MIDDLE_WEST
					LOWER  -> Companion.SHAPE_LOWER_WEST
					else   -> Shapes.block()
				}
			}
			EAST  -> {
				when (state.getValue(Companion.TRIPLE_HALF)) {
					UPPER  -> Companion.SHAPE_UPPER_EAST
					MIDDLE -> Companion.SHAPE_MIDDLE_EAST
					LOWER  -> Companion.SHAPE_LOWER_EAST
					else   -> Shapes.block()
				}
			}
			else  -> Shapes.block()
		}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val half = state.getValue(Companion.TRIPLE_HALF)
		val newPos = if (half == MIDDLE) pos.below() else if (half == UPPER) pos.below().below() else pos
		val normalizedPos = normalizedHitPos(hitResult.location, pos)
		val direction = state.getValue(Companion.FACING)
		val doubleButtonState =
			directionalTargetFaceSection(direction, normalizedPos, 0.29, 0.46, 0.62, 0.81, 0.49, 0.59)
		val cashOutButtonState =
			directionalTargetFaceSection(direction, normalizedPos, 0.29, 0.46, 0.19, 0.38, 0.49, 0.59)
		val entity = level.getBlockEntity(newPos) as DoubleOrNothingBlockEntity

		if (doubleButtonState || half == MIDDLE || half == UPPER) entity.double(level, player, pos)
		else if (cashOutButtonState) entity.cashout(level, player, pos)

		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun useItemOn(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		if (state.getValue(Companion.TRIPLE_HALF) == LOWER) {
			val entity = level.getBlockEntity(pos) as DoubleOrNothingBlockEntity
			if (stack.`is`(Items.SHEARS)) {
				level.playSound(null, pos, SoundEvents.BEE_STING, BLOCKS, 1f, 1f)
				entity.rewired = !entity.rewired
			} else if (stack.`is`(Items.BOW)) {
				level.playSound(null, pos, SoundEvents.VILLAGER_NO, BLOCKS, 1f, 1f)
				entity.blockhead = !entity.blockhead
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = DoubleOrNothingBlockEntity(pos, state)

	override fun <T : BlockEntity?> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? = BaseEntityBlock.createTickerHelper(
		blockEntityType,
		ModBlockEntityTypes.DOUBLE_OR_NOTHING.get()
	) { tLevel, tPos, tState, tEntity ->
		if (tState.getValue(Companion.TRIPLE_HALF) == LOWER) tEntity.tick(tLevel, tPos, tState)
	}
}