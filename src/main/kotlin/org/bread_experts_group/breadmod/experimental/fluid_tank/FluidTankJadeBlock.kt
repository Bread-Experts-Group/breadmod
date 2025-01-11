package org.bread_experts_group.breadmod.experimental.fluid_tank

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class FluidTankJadeBlock : BaseEntityBlock(Properties.of()) {
	companion object {
		val CODEC: MapCodec<out BaseEntityBlock> = simpleCodec { FluidTankJadeBlock() }
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val entity = level.getBlockEntity(pos) as SidedFluidTankJadeBlockEntity
		entity.sides[Direction.EAST] = entity.tank.tanks[2]
		entity.sides[Direction.WEST] = entity.tank.tanks[3]

		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun codec(): MapCodec<out BaseEntityBlock> = Companion.CODEC
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
		SidedFluidTankJadeBlockEntity(pos, state)

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}