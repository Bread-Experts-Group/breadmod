package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.registries.DeferredHolder
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.entity.block.DoubleOrNothingRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.DoubleOrNothingRenderer.LerpLabels
import org.bread_experts_group.breadmod.network.clientbound.DoubleOrNothingPacket
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TRIPLE_BLOCK
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.LOWER
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.MIDDLE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.UPPER
import org.bread_experts_group.breadmod.registry.block.handler.HitboxHandler
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.BLOCKHEAD
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.CASHOUT
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.DATA
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.DOUBLE_COUNTER
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.JACKPOT
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.JACKPOT_TIMER
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.NOTHING
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.REWIRED
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.USE_NEGATIVE_TILT
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.Hitbox
import org.bread_experts_group.breadmod.util.combine
import org.bread_experts_group.breadmod.util.rotate
import java.util.Random
import java.util.stream.Stream

class DoubleOrNothingBlock : BreadModBlock(Properties.of()) {
	companion object {
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
		).combine()
		val SHAPE_LOWER_SOUTH: VoxelShape = this.SHAPE_LOWER_NORTH.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_LOWER_EAST: VoxelShape = this.SHAPE_LOWER_NORTH.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_LOWER_WEST: VoxelShape = this.SHAPE_LOWER_NORTH.rotate(Rotation.COUNTERCLOCKWISE_90)
		val SHAPE_MIDDLE_NORTH: VoxelShape = Stream.of(
			box(15.0, 0.0, 8.0, 16.0, 16.0, 16.0),
			box(1.0, 0.0, 9.0, 15.0, 16.0, 16.0),
			box(0.0, 0.0, 8.0, 1.0, 16.0, 16.0)
		).combine()
		val SHAPE_MIDDLE_SOUTH: VoxelShape = this.SHAPE_MIDDLE_NORTH.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_MIDDLE_EAST: VoxelShape = this.SHAPE_MIDDLE_NORTH.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_MIDDLE_WEST: VoxelShape = this.SHAPE_MIDDLE_NORTH.rotate(Rotation.COUNTERCLOCKWISE_90)

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
		).combine()
		val SHAPE_UPPER_SOUTH: VoxelShape = this.SHAPE_UPPER_NORTH.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_UPPER_EAST: VoxelShape = this.SHAPE_UPPER_NORTH.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_UPPER_WEST: VoxelShape = this.SHAPE_UPPER_NORTH.rotate(Rotation.COUNTERCLOCKWISE_90)
		val SHEARS_TAG: TagKey<Item> = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/shear"))
		val BOWS_TAG: TagKey<Item> = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/bow"))
		val SOUNDS: List<DeferredHolder<SoundEvent, SoundEvent>> = listOf(
			ModSounds.DOUBLE_1X,
			ModSounds.DOUBLE_2X,
			ModSounds.DOUBLE_3X,
			ModSounds.DOUBLE_4X,
			ModSounds.DOUBLE_5X,
			ModSounds.DOUBLE_6X,
			ModSounds.DOUBLE_7X,
			ModSounds.DOUBLE_8X,
			ModSounds.DOUBLE_9X,
			ModSounds.DOUBLE_JACKPOT
		)
	}

	fun applyZoom(entity: BreadModBlockEntity, state: DoubleOrNothingStateHandler) {
		val lerpTicker = entity.getLerpTicker<LerpLabels>()
		lerpTicker.setParamPosition(
			LerpLabels.ZOOM, when (state.get(DOUBLE_COUNTER)) {
				0, 1, 2, 3 -> 0.3f
				4 -> 0.4f
				5 -> 0.42f
				6 -> 0.47f
				7 -> 0.50f
				8 -> 0.52f
				9 -> 0.55f
				else -> 0f
			}
		)
	}

	fun applyTilt(entity: BreadModBlockEntity, state: DoubleOrNothingStateHandler) {
		val lerpTicker = entity.getLerpTicker<LerpLabels>()
		val doubles = state.get(DOUBLE_COUNTER)
		if (doubles < 4) return
		val tiltIntensity = when (doubles) {
			4 -> 10f
			5 -> 13f
			6 -> 15f
			7 -> 17f
			8 -> 19f
			9 -> 22f
			else -> 0f
		}
		lerpTicker.setParamPosition(LerpLabels.TILT_P, tiltIntensity)
		lerpTicker.setParamPosition(LerpLabels.TILT_N, -tiltIntensity)
		state.set(USE_NEGATIVE_TILT, kotlin.random.Random.nextBoolean())
	}

	fun handleDouble(
		entity: BreadModBlockEntity, state: DoubleOrNothingStateHandler,
		nothing: Boolean
	) {
		val doubles = state.get(DOUBLE_COUNTER)
		this.applyZoom(entity, state)
		if (nothing) {
			state.set(DOUBLE_COUNTER, 0)
			state.set(NOTHING, true)
		} else {
			this.applyTilt(entity, state)
			if (doubles < 10) state.set(DOUBLE_COUNTER, doubles + 1)
			if (doubles == 10) state.set(JACKPOT, true)
		}
	}

	fun triggerDouble(level: Level, pos: BlockPos, player: Player) {
		if (level !is ServerLevel) return
		val entity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return
		val state = entity.getCapability(DoubleOrNothingStateHandler.BLOCK_VOID)
		if (state.get(JACKPOT)) return
		if (state.get(NOTHING)) state.set(NOTHING, false)
		if (state.getOrNull(DATA) == null) state.set(
			DATA,
			DoubleOrNothingStateHandler.DoubleOrNothingData(player, level.gameTime)
		)
		fun playSound(level: Level, pos: BlockPos, sound: DeferredHolder<SoundEvent, SoundEvent>) = level.playSound(
			null, pos.above(), sound.get(), SoundSource.BLOCKS,
			1f, 1f
		)

		val doubles = Random().nextInt(0, 10) >= (if (state.get(REWIRED)) 0 else 4) || state.get(DOUBLE_COUNTER) == 0
		playSound(level, pos, if (doubles) Companion.SOUNDS[state.get(DOUBLE_COUNTER)] else ModSounds.DOUBLE_NOTHING)
		PacketDistributor.sendToPlayersTrackingChunk(
			level, ChunkPos(pos),
			DoubleOrNothingPacket(pos, !doubles)
		)
		this.handleDouble(entity, state, !doubles)
	}

	fun triggerCashout(level: Level, pos: BlockPos, player: Player) {
		this.logger.info("CASHOUT")
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean {
		if (with == null) return true
		return when (with.second.getValue(TRIPLE_BLOCK)) {
			LOWER -> true
			else -> false
		}
	}

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val state = DoubleOrNothingStateHandler()
		val lerp = LerpTickerHandler(
			LerpLabels.ZOOM to LerpTicker.LerpParams(clampMin = 0f, clampMax = 0.55f),
			LerpLabels.TILT_P to LerpTicker.LerpParams(clampMin = 0f, clampMax = 20f),
			LerpLabels.TILT_N to LerpTicker.LerpParams(clampMin = -20f, clampMax = 0f)
		)
		return mapOf(
			DoubleOrNothingStateHandler.BLOCK_VOID to mapOf(null to { _ -> state }),
			LerpTickerHandler.BLOCK_VOID to mapOf(null to { _ -> lerp }),
			HitboxHandler.BLOCK_VOID to mapOf(null to { entity ->
				val pos = entity.blockPos
				val facing = entity.blockState.getValue(HORIZONTAL_FACING)
				val offset = pos.relative(facing).center
				val nsOffset = if (facing == Direction.SOUTH) 0.219 else if (facing == Direction.NORTH) -0.219 else 0.0
				val ewOffset = if (facing == Direction.WEST) 0.219 else if (facing == Direction.EAST) -0.219 else 0.0
				HitboxHandler(
					Hitbox(
						0.185,
						pos,
						offset.relative(facing, -0.88).add(nsOffset, 0.0, ewOffset)
					) { level, pos, state, player, entity ->
						this.triggerDouble(level, pos, player)
					},
					Hitbox(
						0.185,
						pos,
						offset.relative(facing, -0.88).add(-nsOffset, 0.0, -ewOffset)
					) { level, pos, state, player, entity ->
						this.triggerCashout(level, pos, player)
					}
				)
			})
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::DoubleOrNothingRenderer

	override val commonTickBM: BreadModTicker<Level> = { entity, level, _, pos ->
		val ticker = entity.getLerpTicker<LerpLabels>()
		fun tickZoom(params: (LerpTicker.LerpParams) -> Unit): Unit = ticker.tickCustom(LerpLabels.ZOOM, params)
		fun tickTilts(params: (LerpTicker.LerpParams) -> Unit) {
			ticker.tickCustom(LerpLabels.TILT_P, params)
			ticker.tickCustom(LerpLabels.TILT_N, params)
		}

		val state = entity.getCapability(DoubleOrNothingStateHandler.BLOCK_VOID)
		fun reset() {
			state.set(DATA, null)
			state.set(DOUBLE_COUNTER, 0)
			state.set(CASHOUT, false)
			ticker.setParamPosition(LerpLabels.ZOOM, 0.3f)
			ticker.setParamPosition(LerpLabels.TILT_P, 0f)
		}

		state.set(REWIRED, true)
		val multiplier = when (state.get(DOUBLE_COUNTER)) {
			7 -> 0.7f
			8 -> 0.5f
			9 -> 0.2f
			else -> 1f
		}

		tickZoom { it.setClampedPos(-0.1f * multiplier) }
		tickTilts {
			if (it.clampMax == 0f) it.setClampedPos(2f * multiplier + 0.05f) else it.setClampedPos(-2f * multiplier + 0.05f)
		}

		state.getOrNull(DATA)?.let { data ->
			if (data.startedAt + 600 < level.gameTime
				&& !state.get(JACKPOT)
				&& !state.get(NOTHING)
				&& !state.get(CASHOUT)
				&& data.startedAt != 0L
			) reset()

			if (state.get(CASHOUT) && data.startedAt + 60 == level.gameTime) reset()

			if (state.get(JACKPOT)) {
				state.set(JACKPOT_TIMER, state.get(JACKPOT_TIMER) + 1)
				if (state.get(JACKPOT_TIMER) > 1200) {
					state.set(JACKPOT, false)
					state.set(JACKPOT_TIMER, 0)
					reset()
				}
			}

			if (state.get(NOTHING)) {
				if (data.startedAt + 30 == level.gameTime) {
					reset()
					state.set(NOTHING, false)
				}
			}
		}
		this.synchronizeEntity(entity)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(TRIPLE_BLOCK, HORIZONTAL_FACING)
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
				.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
				.setValue(TRIPLE_BLOCK, LOWER)
		} else null
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

	override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		level.setBlockAndUpdate(pos.above(), state.setValue(TRIPLE_BLOCK, MIDDLE))
		level.setBlockAndUpdate(pos.above().above(), state.setValue(TRIPLE_BLOCK, UPPER))
	}

	override fun updateShape(
		state: BlockState,
		facing: Direction,
		neighborState: BlockState,
		level: LevelAccessor,
		pos: BlockPos,
		neighborPos: BlockPos
	): BlockState {
		val half = state.getValue(TRIPLE_BLOCK)
		// todo only breaks all three blocks when the middle or lower block is broken, upper block doesn't break the other two.
		//  look into DoorBlock and try to make a three block tall variant that works properly..
		return if (facing.axis != Direction.Axis.Y || (half == LOWER != (facing == Direction.UP))) {
			if (half == LOWER && facing == Direction.DOWN) {
				Blocks.AIR.defaultBlockState()
			} else super.updateShape(state, facing, neighborState, level, pos, neighborPos)
		} else {
			if (neighborState.block is DoubleOrNothingBlock && neighborState.getValue(TRIPLE_BLOCK) != half)
				neighborState.setValue(TRIPLE_BLOCK, half) else Blocks.AIR.defaultBlockState()
		}
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		when (state.getValue(HORIZONTAL_FACING)) {
			Direction.NORTH -> {
				when (state.getValue(TRIPLE_BLOCK)) {
					UPPER -> Companion.SHAPE_UPPER_NORTH
					MIDDLE -> Companion.SHAPE_MIDDLE_NORTH
					LOWER -> Companion.SHAPE_LOWER_NORTH
					else -> Shapes.block()
				}
			}
			Direction.SOUTH -> {
				when (state.getValue(TRIPLE_BLOCK)) {
					UPPER -> Companion.SHAPE_UPPER_SOUTH
					MIDDLE -> Companion.SHAPE_MIDDLE_SOUTH
					LOWER -> Companion.SHAPE_LOWER_SOUTH
					else -> Shapes.block()
				}
			}
			Direction.WEST -> {
				when (state.getValue(TRIPLE_BLOCK)) {
					UPPER -> Companion.SHAPE_UPPER_WEST
					MIDDLE -> Companion.SHAPE_MIDDLE_WEST
					LOWER -> Companion.SHAPE_LOWER_WEST
					else -> Shapes.block()
				}
			}
			Direction.EAST -> {
				when (state.getValue(TRIPLE_BLOCK)) {
					UPPER -> Companion.SHAPE_UPPER_EAST
					MIDDLE -> Companion.SHAPE_MIDDLE_EAST
					LOWER -> Companion.SHAPE_LOWER_EAST
					else -> Shapes.block()
				}
			}
			else -> Shapes.block()
		}

//	override fun useWithoutItem(
//		state: BlockState,
//		level: Level,
//		pos: BlockPos,
//		player: Player,
//		hitResult: BlockHitResult
//	): InteractionResult {
//		val half = state.getValue(TRIPLE_BLOCK)
//		val normalizedPos = normalizedHitPos(hitResult.location, pos)
//		val direction = state.getValue(HORIZONTAL_FACING)
//		val doubleButtonState = directionalTargetFaceSection(
//			direction, normalizedPos,
//			0.29, 0.46, 0.62, 0.81,
//			0.49, 0.59
//		)
//		val cashOutButtonState = directionalTargetFaceSection(
//			direction, normalizedPos,
//			0.29, 0.46, 0.19, 0.38,
//			0.49, 0.59
//		)
//		when {
//			doubleButtonState -> this.triggerDouble(level, pos, player)
//			cashOutButtonState -> this.triggerCashout(level, pos, player)
//			half == MIDDLE -> this.triggerDouble(level, pos.below(1), player)
//			half == UPPER -> this.triggerDouble(level, pos.below(2), player)
//		}
//		return InteractionResult.sidedSuccess(level.isClientSide)
//	}

	override fun useItemOnBM(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		val entity = level.getBlockEntity(pos) as? BreadModBlockEntity
			?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
		val entityState = entity.getCapability(DoubleOrNothingStateHandler.BLOCK_VOID)
		if (state.getValue(TRIPLE_BLOCK) == LOWER) {
			if (stack.`is`(Companion.SHEARS_TAG)) {
				level.playSound(null, pos, SoundEvents.BEE_STING, SoundSource.BLOCKS, 1f, 1f)
				entityState.invert(REWIRED)
			} else if (stack.`is`(Companion.BOWS_TAG)) {
				level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1f, 1f)
				entityState.invert(BLOCKHEAD)
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
	}
}