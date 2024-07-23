package breadmod.block

import breadmod.block.entity.HeatingElementBlockEntity
import breadmod.registry.block.ModBlockEntityTypes.HEATING_ELEMENT
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d

class HeatingElementBlock: Block(
    Properties.copy(Blocks.IRON_BLOCK)
        .ignitedByLava()
        .lightLevel { pState -> if(pState.getValue(BlockStateProperties.POWERED)) 15 else 0 }
        .emissiveRendering { pState, _, _ -> pState.getValue(BlockStateProperties.POWERED) }
), EntityBlock
{
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(DirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.POWERED, false)
        )
    }

    override fun stepOn(pLevel: Level, pPos: BlockPos, pState: BlockState, pEntity: Entity) {
        if(pState.getValue(BlockStateProperties.POWERED)) pEntity.setSecondsOnFire(3)
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if(pState.getValue(BlockStateProperties.POWERED)) {
            val position = pPos.toVector3d().plus(Vector3d(
                when(Direction.getRandom(pRandom).axis.plane) {
                    Direction.Plane.HORIZONTAL -> 0.0
                    Direction.Plane.VERTICAL -> 1.0
                    else -> throw IllegalStateException("")
                },
                pRandom.nextDouble(),
                pRandom.nextDouble()
            ))
            pLevel.addParticle(ParticleTypes.SMOKE, position.x, position.y, position.z, 0.0,0.0, 0.0)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? =
        if (type == HEATING_ELEMENT.get()) (HeatingElementBlockEntity.Companion as BlockEntityTicker<T>) else null

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(DirectionalBlock.FACING, pContext.nearestLookingDirection)
    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING, BlockStateProperties.POWERED) }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState) = HeatingElementBlockEntity(pPos, pState)
}