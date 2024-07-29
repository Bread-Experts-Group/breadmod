package breadmod.block.multiblock.farmer

import breadmod.block.entity.multiblock.generic.PowerInterfaceBlockEntity
import breadmod.registry.block.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.energy.EnergyStorage

class FarmerControllerBlock: Block(Properties.of()
    .strength(2.0f, 6.0f)
    .mapColor(MapColor.COLOR_BROWN)
    .sound(SoundType.COPPER)
) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.TRIGGERED, false)
        )
    }

    override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean = !player.isCreative

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(HorizontalDirectionalBlock.FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(HorizontalDirectionalBlock.FACING, BlockStateProperties.TRIGGERED)
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        val aabb = AABB(pPos.offset(-1, 0, 0), pPos.offset(1,2,2))
//        println("blockstate AABB")
//        println(pLevel.getBlockStates(aabb).map { it.block }.toList())

        println("cursed block entity fetcher")
        BlockPos.betweenClosedStream(aabb).forEach { subPos ->
            if(pLevel.getBlockState(subPos).`is`(ModBlocks.GENERIC_POWER_INTERFACE.get().block)) {
                val entity = pLevel.getBlockEntity(BlockPos(subPos.x, subPos.y, subPos.z)) as? PowerInterfaceBlockEntity
                    ?: return@forEach
                println(entity.capabilityHolder.capabilityOrNull<EnergyStorage>(ForgeCapabilities.ENERGY)?.energyStored)
            }

            println("Block and pos: ${BlockPos(subPos.x, subPos.y, subPos.z)}, Relative to controller: ${BlockPos(subPos.x - pPos.x, subPos.y - pPos.y, subPos.z - pPos.z)} : ${pLevel.getBlockState(subPos).block}")
        }
        return InteractionResult.PASS
    }
}