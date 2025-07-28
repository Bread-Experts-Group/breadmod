package org.bread_experts_group.breadmod.registry.block.actual
//class MicrowaveBlock : BreadModBlockWithEntity(Properties.of()) {
//	private companion object {
//		val HORIZONTAL_FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
//		val OPEN: BooleanProperty = BlockStateProperties.OPEN
//		val SHAPE_NORTH: VoxelShape = Stream.of(
//			box(1.0, 1.0, 3.0, 15.0, 9.0, 13.0),
//			box(2.0, 2.0, 13.0, 14.0, 8.0, 14.0),
//			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
//			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5),
//			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
//			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5)
//		).combine()
//		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.rotate(CLOCKWISE_180)
//		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.rotate(CLOCKWISE_90)
//		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.rotate(COUNTERCLOCKWISE_90)
//		val SHAPE_NORTH_OPEN: VoxelShape = Stream.of(
//			box(1.0, 1.0, 3.0, 5.0, 9.0, 13.0),
//			box(5.0, 1.0, 3.0, 15.0, 2.0, 13.0),
//			box(6.5, 2.0, 5.0, 12.5, 2.5, 11.0),
//			box(14.0, 2.0, 3.0, 15.0, 8.0, 13.0),
//			box(5.0, 2.0, 12.0, 14.0, 8.0, 14.0),
//			box(2.0, 2.0, 13.0, 5.0, 8.0, 14.0),
//			box(5.0, 8.0, 3.0, 15.0, 9.0, 13.0),
//			box(12.5, 0.0, 3.5, 14.5, 1.0, 5.5),
//			box(1.5, 0.0, 3.5, 3.5, 1.0, 5.5),
//			box(1.5, 0.0, 10.5, 3.5, 1.0, 12.5),
//			box(12.5, 0.0, 10.5, 14.5, 1.0, 12.5)
//		).combine()
//		val SHAPE_SOUTH_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(CLOCKWISE_180)
//		val SHAPE_EAST_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(CLOCKWISE_90)
//		val SHAPE_WEST_OPEN: VoxelShape = this.SHAPE_NORTH_OPEN.rotate(COUNTERCLOCKWISE_90)
//	}
//
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MicrowaveBlockEntity(pos, state)
//	override fun useWithoutItem(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hitResult: BlockHitResult
//	): InteractionResult {
//		val direction = hitResult.direction ?: return FAIL
//		val normalizedPos = normalizedHitPos(hitResult.location, pos)
//		val entity = level.getBlockEntity(pos) as MicrowaveBlockEntity
//		val number = when (direction) {
//			NORTH -> this.buttonPress(normalizedPos.x, normalizedPos.y, 0.23, 0.27, level, pos, NORTH)
//			EAST  -> this.buttonPress(normalizedPos.z, normalizedPos.y, 0.23, 0.27, level, pos, EAST)
//			SOUTH -> this.buttonPress(normalizedPos.x, normalizedPos.y, 0.73, 0.77, level, pos, SOUTH)
//			WEST  -> this.buttonPress(normalizedPos.z, normalizedPos.y, 0.73, 0.77, level, pos, WEST)
//			else  -> -1
//		}
//		if (entity.getItem(0).isEmpty) entity.setItem(0, player.getItemInHand(player.usedItemHand))
//		return sidedSuccess(level.isClientSide)
//	}
//
//	private fun sound(level: Level, pos: BlockPos, pitch: Float): Unit =
//		level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), BLOCKS, 1f, pitch)
//
//	private fun buttonPress(
//		targetX: Double,
//		targetY: Double,
//		startMinX: Double,
//		startMaxX: Double,
//		level: Level,
//		pos: BlockPos,
//		facing: Direction
//	): Int {
//		repeat(4) { yIndex ->
//			val yMul = (yIndex / 100.0) * 5
//
//			repeat(3) { xIndex ->
//				val xMul = (xIndex / 100.0) * 6
//				val pitchMul = (xIndex / 100.0f) * 2
//				if (targetFaceSection(
//						targetX, targetY,
//						if (facing == NORTH || facing == EAST) startMinX - xMul else startMinX + xMul,
//						0.3 - yMul,
//						if (facing == NORTH || facing == EAST) startMaxX - xMul else startMaxX + xMul,
//						0.33 - yMul
//					)
//				) {
//					this.sound(level, pos, 0.5f + pitchMul + yMul.toFloat())
//					return (xIndex + 1) + (yIndex * 3)
//				}
//			}
//		}
//		return 0
//	}
//
//	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
//		builder.add(Companion.HORIZONTAL_FACING, Companion.OPEN)
//	}
//
//	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
//		this.defaultBlockState()
//			.setValue(Companion.HORIZONTAL_FACING, context.horizontalDirection.opposite)
//			.setValue(Companion.OPEN, false)
//
//	override fun getShape(
//		state: BlockState,
//		level: BlockGetter,
//		pos: BlockPos,
//		context: CollisionContext
//	): VoxelShape {
//		val direction = state.getValue(Companion.HORIZONTAL_FACING) ?: return Shapes.block()
//		return if (state.getValue(Companion.OPEN)) {
//			when (direction) {
//				NORTH    -> Companion.SHAPE_NORTH_OPEN
//				SOUTH    -> Companion.SHAPE_SOUTH_OPEN
//				WEST     -> Companion.SHAPE_WEST_OPEN
//				EAST     -> Companion.SHAPE_EAST_OPEN
//				UP, DOWN -> Shapes.block()
//			}
//		} else when (direction) {
//			NORTH    -> Companion.SHAPE_NORTH
//			SOUTH    -> Companion.SHAPE_SOUTH
//			WEST     -> Companion.SHAPE_WEST
//			EAST     -> Companion.SHAPE_EAST
//			UP, DOWN -> Shapes.block()
//		}
//	}
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.MICROWAVE.get()
//}