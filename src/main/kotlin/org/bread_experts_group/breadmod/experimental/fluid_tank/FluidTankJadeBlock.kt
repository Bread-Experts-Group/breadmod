package org.bread_experts_group.breadmod.experimental.fluid_tank

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class FluidTankJadeBlock : BaseEntityBlock(Properties.of()) {
	companion object {
		val CODEC: MapCodec<out BaseEntityBlock> = simpleCodec { FluidTankJadeBlock() }
	}

	override fun codec(): MapCodec<out BaseEntityBlock> = Companion.CODEC
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
		SidedFluidTankJadeBlockEntity(pos, state)

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}