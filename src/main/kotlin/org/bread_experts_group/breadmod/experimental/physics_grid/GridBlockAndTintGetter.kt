package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

interface GridBlockAndTintGetter : BlockAndTintGetter {
	private fun self(): PhysicsGrid = this as PhysicsGrid

	override fun getBlockState(pos: BlockPos): BlockState = this.self().blocks[pos] ?: Blocks.AIR.defaultBlockState()
	fun getBlockState(x: Int, y: Int, z: Int): BlockState = this.getBlockState(BlockPos(x, y, z))
	override fun getBlockEntity(pos: BlockPos): BlockEntity? = null // TODO BlockEntity support?
	override fun getFluidState(pos: BlockPos): FluidState = this.self().fluids[pos] ?: Fluids.EMPTY.defaultFluidState()
	override fun getHeight(): Int = Int.MAX_VALUE
	override fun getMinBuildHeight(): Int = Int.MIN_VALUE
	override fun getShade(direction: Direction, shade: Boolean): Float =
		this.self().level.getShade(direction, shade)

	override fun getBlockTint(blockPos: BlockPos, colorResolver: ColorResolver): Int =
		this.self().level.getBlockTint(blockPos, colorResolver)

	override fun getLightEngine(): LevelLightEngine = this.self().level.lightEngine
}