package org.bread_experts_group.breadmod.registry.block.actual.machine
//class CreativeGeneratorBlock : BreadModBlockWithEntity(
//	Properties.ofFullCopy(Blocks.COPPER_BLOCK)
//		.lightLevel { 6 }
//) {
//	companion object {
//		val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
//		val ENABLED: BooleanProperty = BlockStateProperties.ENABLED
//		val SHAPE_NORTH: VoxelShape = Stream.of(
//			box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
//			box(1.0, 1.0, 1.0, 15.0, 2.0, 15.0),
//			box(2.0, 2.0, 2.0, 3.0, 3.0, 14.0),
//			box(13.0, 2.0, 2.0, 14.0, 3.0, 14.0),
//			box(3.0, 2.0, 2.0, 13.0, 3.0, 3.0),
//			box(3.0, 2.0, 13.0, 13.0, 3.0, 14.0),
//			box(1.0, 3.0, 15.0, 15.0, 4.0, 16.0),
//			box(1.0, 12.0, 15.0, 15.0, 13.0, 16.0),
//			box(4.0, 4.0, 15.0, 12.0, 12.0, 16.0),
//			box(5.0, 3.0, 5.0, 11.0, 4.0, 11.0),
//			box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0),
//			box(5.0, 12.0, 5.0, 11.0, 15.0, 11.0),
//			box(5.0, 15.0, 5.0, 11.0, 16.0, 11.0),
//			box(1.0, 15.0, 4.0, 15.0, 16.0, 5.0),
//			box(1.0, 15.0, 11.0, 15.0, 16.0, 12.0),
//			box(1.0, 15.0, 15.0, 15.0, 16.0, 16.0),
//			box(1.0, 15.0, 0.0, 15.0, 16.0, 1.0),
//			box(0.0, 15.0, 0.0, 1.0, 16.0, 16.0),
//			box(15.0, 15.0, 0.0, 16.0, 16.0, 16.0),
//			box(15.0, 12.0, 1.0, 16.0, 13.0, 15.0),
//			box(0.0, 12.0, 1.0, 1.0, 13.0, 15.0),
//			box(0.0, 3.0, 1.0, 1.0, 4.0, 15.0),
//			box(15.0, 3.0, 1.0, 16.0, 4.0, 15.0),
//			box(15.0, 4.0, 4.0, 16.0, 12.0, 12.0),
//			box(0.0, 4.0, 4.0, 1.0, 12.0, 12.0),
//			box(9.0, 1.0, 0.0, 15.0, 4.0, 1.0),
//			box(0.0, 1.0, 0.0, 1.0, 15.0, 1.0),
//			box(0.0, 1.0, 15.0, 1.0, 15.0, 16.0),
//			box(15.0, 1.0, 15.0, 16.0, 15.0, 16.0),
//			box(15.0, 1.0, 0.0, 16.0, 15.0, 1.0)
//		).combine()
//		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.rotate(CLOCKWISE_180)
//		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.rotate(CLOCKWISE_90)
//		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.rotate(COUNTERCLOCKWISE_90)
//	}
//
//	private val random: RandomSource = RandomSource.create()
//	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
//		CreativeGeneratorBlockEntity(pos, state)
//
//	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
//		builder.add(Companion.FACING, Companion.ENABLED)
//	}
//
//	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
//		this.defaultBlockState()
//			.setValue(Companion.FACING, context.horizontalDirection.opposite)
//			.setValue(Companion.ENABLED, true)
//
//	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
//		when (state.getValue(Companion.FACING)) {
//			NORTH -> Companion.SHAPE_NORTH
//			SOUTH -> Companion.SHAPE_SOUTH
//			EAST  -> Companion.SHAPE_EAST
//			WEST  -> Companion.SHAPE_WEST
//			else  -> Shapes.block()
//		}
//
//	override fun neighborChanged(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		neighborBlock: Block,
//		neighborPos: BlockPos,
//		movedByPiston: Boolean
//	) {
//		if (level.hasNeighborSignal(pos) && state.getValue(Companion.ENABLED)) {
//			level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, BLOCKS, 1f, 1f)
//			level.setBlockAndUpdate(pos, state.setValue(Companion.ENABLED, false))
//			this.sendParticles(level, pos)
//		} else if (!level.hasNeighborSignal(pos) && !state.getValue(Companion.ENABLED)) {
//			level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, BLOCKS, 1f, 1f)
//			level.setBlockAndUpdate(pos, state.setValue(Companion.ENABLED, true))
//			this.sendParticles(level, pos)
//		}
//	}
//
//	private fun sendParticles(level: Level, pos: BlockPos) {
//		val rand = (this.random.nextDouble() - 0.5) * 1.2
//		val center = pos.center
//		if (level is ServerLevel) level.sendParticles(
//			ParticleTypes.END_ROD,
//			center.x,
//			center.y,
//			center.z,
//			10,
//			rand,
//			this.random.nextDouble(),
//			rand,
//			0.1
//		)
//	}
//
//	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
//		ModBlockEntityTypes.CREATIVE_GENERATOR.get()
//}