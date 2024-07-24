package breadmod.block

import breadmod.block.util.ModBlockSetTypes
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

class HellNawButtonBlock: ButtonBlock(
    Properties.of()
        .noCollission()
        .strength(0.5f)
        .pushReaction(PushReaction.DESTROY)
        .mapColor(MapColor.COLOR_ORANGE),
    ModBlockSetTypes.HELL_NAW, 10, false
) {
    private val floorAABB = Block.box(5.0, 0.0, 5.0, 11.0, 1.4, 11.0)
    private val floorPressedAABB = Block.box(5.0, 0.0, 5.0, 11.0, 0.9, 11.0)
    private val ceilingAABB = Block.box(5.0, 14.6, 5.0, 11.0, 16.0, 11.0)
    private val ceilingPressedAABB = Block.box(5.0, 15.1, 5.0, 11.0, 16.0, 11.0)
    private val southAABB = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 1.4)
    private val southPressedAABB = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 0.9)
    private val northAABB = Block.box(5.0, 5.0, 14.6, 11.0, 11.0, 16.0)
    private val northPressedAABB = Block.box(5.0, 5.0, 15.1, 11.0, 11.0, 16.0)
    private val westAABB = Block.box(14.6, 5.0, 5.0, 16.0, 11.0, 11.0)
    private val westPressedAABB = Block.box(15.1, 5.0, 5.0, 16.0, 11.0, 11.0)
    private val eastAABB = Block.box(0.0, 5.0, 5.0, 1.4, 11.0, 11.0)
    private val eastPressedAABB = Block.box(0.0, 5.0, 5.0, 0.9, 11.0, 11.0)

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.getShape(pState, pLevel, pPos, pContext)",
        "net.minecraft.world.level.block.ButtonBlock"
    ))
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        val direction = pState.getValue(FACING)
        val flag = pState.getValue(POWERED)
        return when(pState.getValue(FACE) as AttachFace) {
            AttachFace.FLOOR -> if(flag) floorPressedAABB else floorAABB
            AttachFace.CEILING -> if(flag) ceilingPressedAABB else ceilingAABB
            AttachFace.WALL -> {
                val voxelShape = when(direction) {
                    Direction.EAST -> if(flag) eastPressedAABB else eastAABB
                    Direction.WEST -> if(flag) westPressedAABB else westAABB
                    Direction.SOUTH -> if(flag) southPressedAABB else southAABB
                    Direction.DOWN, Direction.UP, Direction.NORTH -> if(flag) northPressedAABB else northAABB
                    else -> throw RuntimeException("invalid direction")
                }
                return voxelShape
            }
        }
    }
}