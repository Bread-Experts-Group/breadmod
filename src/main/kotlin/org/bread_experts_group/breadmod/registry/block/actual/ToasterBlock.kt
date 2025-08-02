package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.sidedSuccess
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.TRIGGERED
import net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.render.entity.block.ToasterRenderer
import org.bread_experts_group.breadmod.datagen.tag.EXPLODES_IN_TOASTER
import org.bread_experts_group.breadmod.datagen.tag.TOASTABLE
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.block.handler.FERecipeHandler
import org.bread_experts_group.breadmod.registry.block.handler.FERecipeHandler.Companion.getRecipeHandler
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.util.Color.RED
import java.math.BigDecimal
import kotlin.random.Random

class ToasterBlock : BreadModBlock(
	Properties.of()
		.strength(1f, 1.0f)
		.mapColor(MapColor.TERRACOTTA_WHITE)
		.sound(SoundType.COPPER)
) {
	private companion object {
		val AABB_X: VoxelShape = box(5.0, 0.0, 2.0, 11.0, 7.0, 14.0)
		val AABB_Z: VoxelShape = box(2.0, 0.0, 5.0, 14.0, 7.0, 11.0)
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::ToasterRenderer

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val storage = ExtendedItemHandler(ExtendedItemHandler.Slot(BigDecimal.TWO))
		val recipe = FERecipeHandler(ModRecipeTypes.TOASTING.get())
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(null to { _ -> storage }),
			FERecipeHandler.BLOCK_VOID to mapOf(null to { _ -> recipe })
		)
	}

	override val serverTickBM: BreadModTicker<ServerLevel> = tick@{ entity, level, state, pos ->
		val recipeHandler = entity.getRecipeHandler<ToasterRecipe>()
		val recipe = recipeHandler.recipe ?: return@tick
		level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true))
		if (recipeHandler.progress == 0uL) recipe.value.consumeItemsAndFluids(recipeHandler.input)
		if (recipeHandler.advanceRecipe()) {
			level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false))
		}
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(TRIGGERED, false)
			.setValue(BlockStateProperties.WATERLOGGED, false)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, TRIGGERED, WATERLOGGED)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = when (state.getValue(HORIZONTAL_FACING)) {
		Direction.NORTH, Direction.SOUTH -> Companion.AABB_X
		else -> Companion.AABB_Z
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val blockEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return InteractionResult.FAIL
		val storageState = blockEntity.getCapability(Capabilities.ItemHandler.BLOCK) as ExtendedItemHandler
		val recipeState = blockEntity.getCapability(FERecipeHandler.BLOCK_VOID)
		val triggeredState = state.getValue(TRIGGERED)
		if (player.isCrouching && recipeState.progress == 0uL) {
			level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, triggeredState))
		} else if (
			!player.isCrouching && !triggeredState && recipeState.progress == 0uL &&
			player.getItemInHand(player.usedItemHand).isEmpty
		) storageState.dropContents(pos, level)
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
		val blockEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return ItemInteractionResult.FAIL
		val storageState = blockEntity.getCapability(Capabilities.ItemHandler.BLOCK) as ExtendedItemHandler
		val recipeState = blockEntity.getCapability(FERecipeHandler.BLOCK_VOID)
		val triggeredState = state.getValue(TRIGGERED)
		if (
			!triggeredState && recipeState.progress <= 0uL && !stack.isEmpty &&
			(stack.`is`(TOASTABLE) || stack.`is`(EXPLODES_IN_TOASTER))
		) {
			val stack = player.getItemInHand(player.usedItemHand)
			val inserted = storageState.insertItem(
				0,
				player.getItemInHand(player.usedItemHand),
				false
			)
			if (inserted.count != stack.count) {
				if (!player.isCreative) stack.count = inserted.count
				level.playSound(
					null,
					pos,
					SoundEvents.ITEM_PICKUP,
					SoundSource.BLOCKS,
					0.2f,
					Random.nextFloat() - 0.3f
				)
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
	}

	// Pretty much a clone of the furnace animateTick code.
	override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
		val posX = pos.x + 0.4
		val posY = pos.y + 0.5
		val posZ = pos.z + 0.5
		val direction = state.getValue(HORIZONTAL_FACING)
		val axis = direction.axis
		val d1 = random.nextDouble() * 0.6 - 0.3
		val d4 = if (axis == Direction.Axis.X) direction.stepZ * 0.52 else d1 // X
		val d3 = random.nextDouble() * 0.6 / 16.0 // Y
		val d2 = if (axis == Direction.Axis.Z) direction.stepX * 0.52 else d1 // Z

		if (state.getValue(TRIGGERED)) {
			val blockEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return
			val storageState = blockEntity.getCapability(Capabilities.ItemHandler.BLOCK)
			if (storageState.getStackInSlot(0).`is`(EXPLODES_IN_TOASTER)) {
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

	override fun appendHoverTextAdditional(
		stack: ItemStack,
		context: Item.TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(BreadMod.modTranslatable("block", "toaster", "tooltip").withColor(RED))
	}
}