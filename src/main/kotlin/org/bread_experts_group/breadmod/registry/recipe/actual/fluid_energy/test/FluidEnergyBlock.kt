package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.actual.AbstractTickingBlockWithBlockEntity

class FluidEnergyBlock : AbstractTickingBlockWithBlockEntity(Properties.of()) {
	companion object {
		val CODEC : MapCodec<FluidEnergyBlock> = simpleCodec { FluidEnergyBlock() }
	}

	override fun getRenderShape(state : BlockState) : RenderShape = RenderShape.MODEL
	override fun codec() : MapCodec<FluidEnergyBlock> = CODEC
	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity =
		FluidEnergyBlockEntity(pos, state)

	override fun useWithoutItem(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hitResult : BlockHitResult
	) : InteractionResult {
		if (!level.isClientSide) {
			val entity = level.getBlockEntity(pos) as? FluidEnergyBlockEntity ?: return InteractionResult.FAIL
			player.openMenu(entity, pos)
		}
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun onRemove(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		newState : BlockState,
		movedByPiston : Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = level.getBlockEntity(pos) as FluidEnergyBlockEntity
			entity.dropContents()
		}
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}