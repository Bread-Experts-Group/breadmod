package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.gui.screens.RadioScreen
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.entity.block.RadioRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.RadioRenderer.LerpLabels
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.LerpTickerHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.LerpTickerHandler.Companion.getLerpTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.RadioStateHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.RadioStateHandler.Companion.DISPLAY_FLIP
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2

class RadioBlock : BreadModBlock(
	Properties.of()
		.strength(4f, 6f)
		.mapColor(MapColor.COLOR_GRAY)
		.requiresCorrectToolForDrops()
		.sound(SoundType.METAL)
) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val state = RadioStateHandler()
		val lerp = LerpTickerHandler(
			LerpLabels.TILT_ALPHA to LerpTicker.LerpParams(),
			LerpLabels.TILT_BETA to LerpTicker.LerpParams(clampMin = 0f, clampMax = 3.15f)
		)
		return mapOf(
			RadioStateHandler.BLOCK_VOID to mapOf(null to { _ -> state }),
			LerpTickerHandler.BLOCK_VOID to mapOf(null to { _ -> lerp })
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::RadioRenderer

	override val clientTickBM: BreadModTicker<ClientLevel> = { entity, _, _, pos ->
		fun doMath(input: Float): Float {
			var newInput = input
			while (newInput >= PI.toFloat()) newInput -= (PI * 2).toFloat()
			while (newInput < -PI.toFloat()) newInput += (PI * 2).toFloat()
			return newInput
		}

		val state = entity.getCapability(RadioStateHandler.BLOCK_VOID)
		val lerpTicker = entity.getLerpTicker<LerpLabels>()
		lerpTicker.tickCustom(LerpLabels.TILT_ALPHA) { params ->
			val player = localClient.player ?: return@tickCustom
			val (x, _, z) = pos.center
			val playerX = player.x - x
			val playerZ = player.z - z
			val f2 = doMath(atan2(playerZ, playerX).toFloat() - params.position)
			params.position += f2 * 0.4f
		}

		lerpTicker.tickCustom(LerpLabels.TILT_BETA) { params ->
			val f2 = doMath(atan(state.get(DISPLAY_FLIP)))
			params.setClampedPos(f2 * 0.4f)
		}
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (level.isClientSide) localClient.setScreen(RadioScreen(pos))
		return InteractionResult.sidedSuccess(level.isClientSide)
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
		if (stack.`is`(Items.STICK)) {
			val entity = (level.getBlockEntity(pos) as? BreadModBlockEntity)
				?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
			val state = entity.getCapability(RadioStateHandler.BLOCK_VOID)
			val flip = state.get(DISPLAY_FLIP)
			state.set(DISPLAY_FLIP, if (flip == 90f) -90f else 90f)
			return ItemInteractionResult.sidedSuccess(level.isClientSide)
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)

	override fun onDestroyedByPlayer(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		willHarvest: Boolean,
		fluid: FluidState
	): Boolean {
		StereoSoundInstance.destroy(level.isClientSide, pos)
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}