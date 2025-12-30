package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockSetTypes

class HellNawButtonBlock : ButtonBlock(
	ModBlockSetTypes.HELL_NAW,
	10,
	Properties.of()
		.noCollission()
		.strength(0.5f)
		.pushReaction(PushReaction.DESTROY)
		.mapColor(MapColor.COLOR_ORANGE)
) {
	private val floorAABB: VoxelShape = Block.box(5.0, 0.0, 5.0, 11.0, 1.4, 11.0)
	private val floorPressedAABB: VoxelShape = Block.box(5.0, 0.0, 5.0, 11.0, 0.9, 11.0)
	private val ceilingAABB: VoxelShape = Block.box(5.0, 14.6, 5.0, 11.0, 16.0, 11.0)
	private val ceilingPressedAABB: VoxelShape = Block.box(5.0, 15.1, 5.0, 11.0, 16.0, 11.0)
	private val southAABB: VoxelShape = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 1.4)
	private val southPressedAABB: VoxelShape = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 0.9)
	private val northAABB: VoxelShape = Block.box(5.0, 5.0, 14.6, 11.0, 11.0, 16.0)
	private val northPressedAABB: VoxelShape = Block.box(5.0, 5.0, 15.1, 11.0, 11.0, 16.0)
	private val westAABB: VoxelShape = Block.box(14.6, 5.0, 5.0, 16.0, 11.0, 11.0)
	private val westPressedAABB: VoxelShape = Block.box(15.1, 5.0, 5.0, 16.0, 11.0, 11.0)
	private val eastAABB: VoxelShape = Block.box(0.0, 5.0, 5.0, 1.4, 11.0, 11.0)
	private val eastPressedAABB: VoxelShape = Block.box(0.0, 5.0, 5.0, 0.9, 11.0, 11.0)
	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		val direction = state.getValue(FACING)
		val flag = state.getValue(POWERED)
		return when (state.getValue(FACE) as AttachFace) {
			AttachFace.FLOOR -> if (flag) this.floorPressedAABB else this.floorAABB
			AttachFace.CEILING -> if (flag) this.ceilingPressedAABB else this.ceilingAABB
			AttachFace.WALL -> {
				val voxelShape = when (direction) {
					Direction.EAST -> if (flag) this.eastPressedAABB else this.eastAABB
					Direction.WEST -> if (flag) this.westPressedAABB else this.westAABB
					Direction.SOUTH -> if (flag) this.southPressedAABB else this.southAABB
					Direction.DOWN, Direction.UP, Direction.NORTH -> if (flag) this.northPressedAABB else this.northAABB
					else -> throw RuntimeException("invalid direction")
				}
				return voxelShape
			}
		}
	}
}