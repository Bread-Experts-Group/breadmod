package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

class PhysicsGrid(
	val level: Level,
	val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf(),
	val fluids: MutableMap<BlockPos, FluidState> = mutableMapOf(),
	val blockEntities: MutableMap<BlockPos, BlockEntity> = mutableMapOf()
) {
	var position: Vec3 = Vec3.ZERO
	var center: Vec3 = Vec3.ZERO
	var xRot: Double = 0.0
	var yRot: Double = 0.0
	var zRot: Double = 0.0

	companion object {
		fun create(from: BlockPos, to: BlockPos, level: Level): PhysicsGrid {
			val aabb = AABB.encapsulatingFullBlocks(from, to)
			val map: MutableMap<BlockPos, BlockState> = mutableMapOf()
			BlockPos.betweenClosedStream(aabb).forEach { pos ->
				val state = level.getBlockState(pos)
				val offset = pos.offset(-from)
				if (state.renderShape == INVISIBLE) return@forEach
				map[offset.immutable()] = state
			}
			val grid = PhysicsGrid(level, map)
			grid.center = from.toVec3().div(2.0).subtract(to.toVec3().div(2.0))
			grid.position = from.toVec3()

			return PhysicsGrid(level, map)
		}
	}

	fun getBlockState(pos: BlockPos): BlockState = this.blocks[pos] ?: Blocks.AIR.defaultBlockState()
	fun setBlockState(pos: BlockPos, newState: BlockState): Boolean {
		val oldState = this.getBlockState(pos)
		if (oldState == newState) return false
		this.blocks[pos] = newState
		return true
	}

	fun setPos(x: Double, y: Double, z: Double) {
		this.position = Vec3(x, y, z)
	}

	fun getBlockPosAbsolute(pos: BlockPos): Vec3 = this.position.plus(pos.toVec3())
}