package bread.mod.breadmod.block

import bread.mod.breadmod.block.util.ILightningStrikeAction
import bread.mod.breadmod.util.render.addMultiblockIdentifier
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult

class BreadBlock : Block(
    Properties
        .ofFullCopy(Blocks.HAY_BLOCK)
        .strength(0.5f, 0.5f)
        .lightLevel { state -> if (state.getValue(BlockStateProperties.POWERED)) 8 else 0 }
), ILightningStrikeAction {
    init {
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    private fun centerAABB(pos: BlockPos): AABB = AABB(pos.center, pos.center)
    private fun AABB.threeByThree(): AABB = inflate(1.0, 1.0, 1.0)
    private fun threeByThreeAABB(pos: BlockPos) = centerAABB(pos).threeByThree()
    private var blockCount = 0

    // todo model 3x3x3 bread block "multiblock" and figure out placing logic and interaction logic (DoorBlock for reference)
    /* https://github.com/Commoble/jumbo-furnace/blob/main/src/main/java/net/commoble/jumbofurnace/jumbo_furnace/JumboFurnaceBlock.java */
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (player.usedItemHand == InteractionHand.MAIN_HAND) {
            val direction = hitResult.direction
            val aabb = when (direction) {
                Direction.DOWN -> threeByThreeAABB(pos).move(0.0, 1.0, 0.0)
                Direction.UP -> threeByThreeAABB(pos).move(0.0, -1.0, 0.0)
                Direction.NORTH -> threeByThreeAABB(pos).move(0.0, 0.0, 1.0)
                Direction.SOUTH -> threeByThreeAABB(pos).move(0.0, 0.0, -1.0)
                Direction.WEST -> threeByThreeAABB(pos).move(1.0, 0.0, 0.0)
                Direction.EAST -> threeByThreeAABB(pos).move(-1.0, 0.0, 0.0)
                else -> null
            }
            if (aabb == null) return InteractionResult.FAIL

//            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true))
            if (level.isClientSide) {
                BlockPos.betweenClosedStream(aabb).forEach { subPos ->
                    addMultiblockIdentifier(blockCount, subPos, level.getBlockState(subPos))
                    blockCount++
                }
                blockCount = 0
            }

            level.getBlockStates(aabb).forEach { subState ->
//                LogManager.getLogger().info("$blockCount, ${it.block}")
//                if (level.isClientSide) {
//
//                }
            }

//            println(aabb)
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

//    override fun getRenderShape(state: BlockState): RenderShape =
//        if (state.getValue(BlockStateProperties.LIT)) {
//            RenderShape.INVISIBLE
//        } else RenderShape.MODEL

//    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
//        if (level is ServerLevel && entity.canChangeDimensions()) {
//            val resourceKey =
//                if (level.dimensionType() === ModDimensions.BREAD.first.dimensionType.second) Level.OVERWORLD
//                else ModDimensions.BREAD.second
//            pEntity.changeDimension(level.server.getLevel(resourceKey) ?: return)
//            level.explode(
//                entity,
//                pos.x.toDouble(),
//                pos.y.toDouble(),
//                pos.z.toDouble(),
//                8.0F,
//                true,
//                Level.ExplosionInteraction.BLOCK
//            )
//        }
//    }

    override fun onLightningStruck(pLevel: Level, pPos: BlockPos, pState: BlockState) {
        pLevel.playSound(null, pPos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 2.0F, 1.0F)
        pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, true))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWERED).add(BlockStateProperties.LIT)
    }
}