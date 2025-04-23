package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

abstract class PhysicsGrid(val level: Level) {
	val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf()
	val fluids: MutableMap<BlockPos, FluidState> = mutableMapOf()
	val voxelShapes: MutableMap<BlockPos, VoxelShape> = mutableMapOf()
	var boundingBox: AABB = AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)
	var position: Vec3 = Vec3.ZERO
	// todo center is busted, it's placed on the corner
	var center: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var gridSize: Vec3 = Vec3.ZERO
	val id: Int = PhysicsGridGlobals.idCounter

	fun setBlockData(from: BlockPos, to: BlockPos): PhysicsGrid {
		val aabb = AABB.encapsulatingFullBlocks(from, to)
		BlockPos.betweenClosedStream(aabb).forEach { pos ->
			val state = this.level.getBlockState(pos)
			val offset = pos.offset(-from)
			if (state.renderShape == INVISIBLE) return@forEach
			if (state.fluidState.`is`(Fluids.EMPTY)) this.blocks[offset.immutable()] = state
			else this.fluids[offset.immutable()] = state.fluidState
		}
		return this
	}

	fun setVoxelShapes(from: BlockPos, to: BlockPos): PhysicsGrid {
		val aabb = AABB.encapsulatingFullBlocks(from, to)
		BlockPos.betweenClosedStream(aabb).forEach { pos ->
			val state = this.level.getBlockState(pos)
			val offset = pos.offset(-from)
			this.voxelShapes[offset.immutable()] = state.getShape(this.level, pos)
		}
		return this
	}

	fun setGridData(from: BlockPos, to: BlockPos): PhysicsGrid {
		val aabb = AABB.encapsulatingFullBlocks(from, to)
		this.position = from.toVec3()
		this.center = aabb.center
		this.gridSize = Vec3(aabb.xsize, aabb.ysize, aabb.zsize)
		this.boundingBox = AABB.encapsulatingFullBlocks(from, to)
		return this
	}

	open fun setPos(newPos: Vec3): PhysicsGrid {
		this.position -= this.position - newPos
		this.center -= this.center - newPos
		this.boundingBox.move(this.center)
		return this
	}

	fun getBlockState(x: Int, y: Int, z: Int): BlockState = this.getBlockState(BlockPos(x, y, z))
	fun getBlockState(pos: BlockPos): BlockState = this.blocks[pos] ?: Blocks.AIR.defaultBlockState()
	fun setBlockState(pos: BlockPos, newState: BlockState): Boolean {
		val oldState = this.getBlockState(pos)
		if (oldState == newState) return false
		this.blocks[pos] = newState
		return true
	}

	open fun removeBlock(pos: BlockPos) {
		this.blocks.remove(pos)
		LogManager.getLogger().info("removed the block at $pos")
	}

	fun getBlockPosAbsolute(pos: BlockPos): Vec3 = this.position.plus(pos.toVec3())

	abstract fun tick()

	abstract fun discard()
}