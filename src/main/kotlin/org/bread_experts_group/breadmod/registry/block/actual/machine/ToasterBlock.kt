package org.bread_experts_group.breadmod.registry.block.actual.machine

import net.minecraft.ChatFormatting.RED
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.sidedSuccess
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.tag.ModItemTags
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlockWithEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity

class ToasterBlock : BreadModBlockWithEntity(
	Properties.of()
		.strength(1f, 1.0f)
		.mapColor(MapColor.TERRACOTTA_WHITE)
		.sound(SoundType.COPPER)
) {
	private companion object {
		val AABB_X: VoxelShape = box(5.0, 0.0, 2.0, 11.0, 7.0, 14.0)
		val AABB_Z: VoxelShape = box(2.0, 0.0, 5.0, 14.0, 7.0, 11.0)
		val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
		val RANDOM: RandomSource = RandomSource.create()
		val TRIGGERED: BooleanProperty = BlockStateProperties.TRIGGERED
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(Companion.FACING, context.horizontalDirection.opposite)
			.setValue(Companion.TRIGGERED, false)
			.setValue(BlockStateProperties.WATERLOGGED, false)

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(Companion.FACING, Companion.TRIGGERED, BlockStateProperties.WATERLOGGED)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = when (state.getValue(Companion.FACING)) {
		Direction.NORTH, Direction.SOUTH -> Companion.AABB_X
		else                             -> Companion.AABB_Z
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val entity = level.getBlockEntity(pos) as? ToasterBlockEntity ?: return InteractionResult.FAIL
		val triggeredState = state.getValue(Companion.TRIGGERED)

		if (player.isCrouching && entity.progress == 0) {
			if (triggeredState) {
				level.setBlockAndUpdate(pos, state.setValue(Companion.TRIGGERED, false))
			} else if (!triggeredState) {
				level.setBlockAndUpdate(pos, state.setValue(Companion.TRIGGERED, true))
			}
		} else if (!player.isCrouching && !triggeredState && entity.progress == 0 &&
			player.getItemInHand(player.usedItemHand).isEmpty
		) {
			entity.dropContents(level, pos)
			level.setBlockAndUpdate(pos, state)
		}

		return sidedSuccess(level.isClientSide)
	}

	override fun useItemOnBM(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		val entity = (level.getBlockEntity(pos) as? ToasterBlockEntity) ?: return ItemInteractionResult.FAIL
		val triggeredState = state.getValue(Companion.TRIGGERED)

		if (!triggeredState && entity.progress <= 0 && !stack.isEmpty &&
			entity.itemHandler.getStackInSlot(0).count != 2 &&
			(stack.`is`(ModItemTags.TOASTABLE) || stack.`is`(ModItemTags.EXPLODES_IN_TOASTER))
		) {
			if (!player.isCreative) stack.shrink(1)
			entity.setOrGrowItem(0, ItemStack(stack.item, 1), 1)
			level.playSound(
				null,
				pos,
				SoundEvents.ITEM_PICKUP,
				BLOCKS,
				0.2f,
				Companion.RANDOM.nextFloat() - 0.3f
			)
			level.setBlockAndUpdate(pos, state)
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
	}

	override fun getRenderShape(state: BlockState): RenderShape = ENTITYBLOCK_ANIMATED

	// Pretty much a clone of the furnace animateTick code.
	override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
		val entity = level.getBlockEntity(pos) as? ToasterBlockEntity ?: return
		val posX = pos.x + 0.4
		val posY = pos.y + 0.5
		val posZ = pos.z + 0.5
		val direction = state.getValue(Companion.FACING)
		val axis = direction.axis
		val d1 = random.nextDouble() * 0.6 - 0.3
		val d4 = if (axis == Direction.Axis.X) direction.stepZ * 0.52 else d1 // X
		val d3 = random.nextDouble() * 0.6 / 16.0 // Y
		val d2 = if (axis == Direction.Axis.Z) direction.stepX * 0.52 else d1 // Z

		if (state.getValue(Companion.TRIGGERED)) {
			if (entity.itemHandler.getStackInSlot(0).`is`(ModItemTags.EXPLODES_IN_TOASTER)) {
				level.addParticle(
					ParticleTypes.LAVA,
					posX + d2,
					posY + d3,
					posZ + d4 + if (axis == Direction.Axis.X) -0.1 else 0.0,
					0.0, 0.0, 0.0
				)
				level.addParticle(
					ParticleTypes.LAVA,
					posX + d2 + 0.2,
					posY + d3,
					posZ + d4 + if (axis == Direction.Axis.X) 0.1 else 0.0,
					0.0, 0.0, 0.0
				)
			} else {
				level.addParticle(
					ParticleTypes.SMOKE,
					posX + d2,
					posY + d3,
					posZ + d4 + if (axis == Direction.Axis.X) -0.1 else 0.0,
					0.0, 0.0, 0.0
				)
				level.addParticle(
					ParticleTypes.SMOKE,
					posX + d2 + 0.2,
					posY + d3,
					posZ + d4 + if (axis == Direction.Axis.X) 0.1 else 0.0,
					0.0, 0.0, 0.0
				)
			}
		}
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(BreadMod.modTranslatable("block", "toaster", "tooltip").withStyle(RED))
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = ToasterBlockEntity(pos, state)

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = level.getBlockEntity(pos) as ToasterBlockEntity
			entity.dropContents(level, pos)
		}
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? = createTickerHelper(
		blockEntityType,
		ModBlockEntityTypes.TOASTER.get(),
		this::tickBreadModBlockEntity
	)
}