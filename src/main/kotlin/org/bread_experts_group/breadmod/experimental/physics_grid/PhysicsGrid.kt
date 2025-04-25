package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.util.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import kotlin.math.pow

abstract class PhysicsGrid(val level: Level) : GridBlockAndTintGetter {
	val id: Int = ++PhysicsGridGlobals.idCounter
	val logger: Logger = LogManager.getLogger("PhysicsGrid ${this.id}")
	val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf()
	val fluids: MutableMap<BlockPos, FluidState> = mutableMapOf()
	val voxelShapes: MutableMap<BlockPos, VoxelShape> = mutableMapOf()
	var boundingBox: AABB = AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)
	var position: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var velocity: Vec3 = Vec3.ZERO

	fun getWorldVoxelShapes(): List<VoxelShape> = this.voxelShapes.map { (local, shape) ->
		shape.move(
			local.x + this.position.x,
			local.y + this.position.y,
			local.z + this.position.z
		)
	}

	fun recomputeBlockData(from: BlockPos, to: BlockPos): PhysicsGrid {
		val aabb = AABB.encapsulatingFullBlocks(from, to)
		BlockPos.betweenClosedStream(aabb).forEach { pos ->
			val state = this.level.getBlockState(pos)
			val offset = pos.offset(-from).immutable()
			if (state.renderShape == INVISIBLE) return@forEach
			if (state.fluidState.`is`(Fluids.EMPTY)) {
				this.blocks[offset] = state
				this.voxelShapes[offset] = state.getShape(this, pos)
			} else this.fluids[offset] = state.fluidState
		}
		return this
	}

	fun recomputeGridData(from: BlockPos, to: BlockPos): PhysicsGrid {
		this.position = from.toVec3()
		this.boundingBox = AABB.encapsulatingFullBlocks(from, to)
		return this
	}

	open fun setPos(newPos: Vec3): PhysicsGrid {
		this.position -= this.position - newPos
		return this
	}

	private val airDensity = 1.225
	private val dragCoefficient = 1.05
	private val crossSectionArea = 25 // TODO calculate
	private val mass = 1 // TODO calculate
	open fun tick() {
		val dragAcceleration = (0.5 * this.airDensity * this.dragCoefficient * this.crossSectionArea *
				this.velocity.length().pow(2.0)) / this.mass
		this.velocity = this.velocity.subtract(this.velocity.scale(dragAcceleration / 20))
		// Apply Velocity
		this.position = this.position.add(this.velocity)
	}

	fun discard() {
		PhysicsGridGlobals.grids.remove(this.id)
	}
}