package org.bread_experts_group.breadmod.registry.block.actual.machine

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.actual.AbstractTickingBlockWithBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity

class WheatCrusherBlock : AbstractTickingBlockWithBlockEntity(Properties.of()) {
	companion object {
		val CODEC : MapCodec<WheatCrusherBlock> = simpleCodec { WheatCrusherBlock() }
	}

	override fun codec() : MapCodec<WheatCrusherBlock> = Companion.CODEC
	override fun canHarvestBlock(state : BlockState, level : BlockGetter, pos : BlockPos, player : Player) : Boolean =
		!player.isCreative

	override fun getStateForPlacement(context : BlockPlaceContext) : BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.POWERED, false)

	override fun createBlockStateDefinition(builder : StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
	}

	override fun useWithoutItem(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hitResult : BlockHitResult
	) : InteractionResult {
		if (!level.isClientSide) {
			val entity = level.getBlockEntity(pos) as? WheatCrusherBlockEntity ?: return InteractionResult.FAIL
			player.openMenu(entity, pos)
		}
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity =
		WheatCrusherBlockEntity(pos, state)

	override fun onRemove(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		newState : BlockState,
		movedByPiston : Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = (level.getBlockEntity(pos) as WheatCrusherBlockEntity)
			entity.dropContents()
		}
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}