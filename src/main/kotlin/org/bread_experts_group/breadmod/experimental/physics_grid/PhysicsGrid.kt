package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level.DummyLevel
import org.bread_experts_group.breadmod.util.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import kotlin.math.pow

abstract class PhysicsGrid(level: Level, posA: BlockPos, posB: BlockPos, isClientSide: Boolean) {
	val id: Int = ++PhysicsGridGlobals.idCounter
	val logger: Logger = LogManager.getLogger("PhysicsGrid ${this.id}")
	val voxelShapes: MutableMap<BlockPos, VoxelShape> = mutableMapOf()
	var boundingBox: AABB = AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)
	var position: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var velocity: Vec3 = Vec3.ZERO
	val level: DummyLevel = DummyLevel(level, isClientSide)

	init {
		val aabb = AABB.encapsulatingFullBlocks(posA, posB)
		// Populating Block and VoxelShape Data
		BlockPos.betweenClosedStream(aabb).forEach { pos ->
			val immutablePos = pos.immutable()
			val state = level.getBlockState(immutablePos)
			val offset = immutablePos.offset(-posA)
			if (state.renderShape == INVISIBLE) return@forEach
			this.level.setBlock(offset, state)
			LogManager.getLogger().info("without offset: ${this.level.getBlockState(immutablePos)}")
			LogManager.getLogger().info("with offset: ${this.level.getBlockState(offset)}")
			this.voxelShapes[offset] = state.getShape(this.level, pos)
			val blockEntity = level.getBlockEntity(pos)
			if (blockEntity != null) this.level.setBlockEntity(blockEntity)
		}
		// Recomputing Grid Position and BoundingBox
		this.position = posA.toVec3()
		this.boundingBox = aabb
	}

	fun getWorldVoxelShapes(): List<VoxelShape> = this.voxelShapes.map { (local, shape) ->
		shape.move(
			local.x + this.position.x,
			local.y + this.position.y,
			local.z + this.position.z
		)
	}

	open fun setPos(newPos: Vec3): PhysicsGrid {
		this.position -= this.position - newPos
		return this
	}

	private val airDensity: Double = 1.225
	private val dragCoefficient: Double = 1.05
	private val crossSectionArea: Int = 25 // TODO calculate
	private val mass: Int = 1 // TODO calculate
	open fun tick() {
		val dragAcceleration = (0.5 * this.airDensity * this.dragCoefficient * this.crossSectionArea *
				this.velocity.length().pow(2.0)) / this.mass
		this.velocity = this.velocity.subtract(this.velocity.scale(dragAcceleration / 20))
		this.position = this.position.add(this.velocity)
	}

	fun discard() {
		PhysicsGridGlobals.grids.remove(this.id)
	}
}