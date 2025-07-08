package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.gui.screens.RadioScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.RadioBlockEntity

class RadioBlock : BreadModBlockWithEntity(
	Properties.of()
		.strength(4f, 6f)
		.mapColor(MapColor.COLOR_GRAY)
		.requiresCorrectToolForDrops()
		.sound(SoundType.METAL)
) {
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
			val entity = level.getBlockEntity(pos) as RadioBlockEntity
			if (entity.displayFlip == 90f) entity.displayFlip = -90f else entity.displayFlip = 90f
			return ItemInteractionResult.sidedSuccess(level.isClientSide)
		}
		return super.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

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

	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
		ModBlockEntityTypes.RADIO.get()

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RadioBlockEntity(pos, state)
	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
	override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec { this }
}