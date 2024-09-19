package bread.mod.breadmod.block

import bread.mod.breadmod.block.util.ModBlockSetTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class HellNawButtonBlock : ButtonBlock(
    ModBlockSetTypes.HELL_NAW,
    10,
    Properties.of()
        .noCollission()
        .strength(0.5f)
        .pushReaction(PushReaction.DESTROY)
        .mapColor(MapColor.COLOR_ORANGE)
) {
    private val floorAABB = box(5.0, 0.0, 5.0, 11.0, 1.4, 11.0)
    private val floorPressedAABB = box(5.0, 0.0, 5.0, 11.0, 0.9, 11.0)
    private val ceilingAABB = box(5.0, 14.6, 5.0, 11.0, 16.0, 11.0)
    private val ceilingPressedAABB = box(5.0, 15.1, 5.0, 11.0, 16.0, 11.0)
    private val southAABB = box(5.0, 5.0, 0.0, 11.0, 11.0, 1.4)
    private val southPressedAABB = box(5.0, 5.0, 0.0, 11.0, 11.0, 0.9)
    private val northAABB = box(5.0, 5.0, 14.6, 11.0, 11.0, 16.0)
    private val northPressedAABB = box(5.0, 5.0, 15.1, 11.0, 11.0, 16.0)
    private val westAABB = box(14.6, 5.0, 5.0, 16.0, 11.0, 11.0)
    private val westPressedAABB = box(15.1, 5.0, 5.0, 16.0, 11.0, 11.0)
    private val eastAABB = box(0.0, 5.0, 5.0, 1.4, 11.0, 11.0)
    private val eastPressedAABB = box(0.0, 5.0, 5.0, 0.9, 11.0, 11.0)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val direction = state.getValue(FACING)
        val flag = state.getValue(POWERED)
        return when (state.getValue(FACE) as AttachFace) {
            AttachFace.FLOOR -> if (flag) floorPressedAABB else floorAABB
            AttachFace.CEILING -> if (flag) ceilingPressedAABB else ceilingAABB
            AttachFace.WALL -> {
                val voxelShape = when (direction) {
                    Direction.EAST -> if (flag) eastPressedAABB else eastAABB
                    Direction.WEST -> if (flag) westPressedAABB else westAABB
                    Direction.SOUTH -> if (flag) southPressedAABB else southAABB
                    Direction.DOWN, Direction.UP, Direction.NORTH -> if (flag) northPressedAABB else northAABB
                    else -> throw RuntimeException("invalid direction")
                }
                return voxelShape
            }
        }
    }
}