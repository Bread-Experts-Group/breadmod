package breadmod.block

import breadmod.CommonForgeEventBus.warTimerMap
import breadmod.network.PacketHandler
import breadmod.network.clientbound.war_timer.WarTimerIncrementPacket
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.network.PacketDistributor
import java.util.stream.Stream

class WarTerminalBlock: Block(Properties.of()) {
    val northAABB = Stream.of(
        box(0.0, 6.0, 0.0, 16.0, 7.0, 1.0),
        box(0.0, 0.0, 1.0, 16.0, 7.0, 5.0),
        box(0.0, 0.0, 5.0, 16.0, 16.0, 16.0)
    ).reduce{ v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val southAABB = Stream.of(
        box(0.0, 6.0, 15.0, 16.0, 7.0, 16.0),
        box(0.0, 0.0, 11.0, 16.0, 7.0, 15.0),
        box(0.0, 0.0, 0.0, 16.0, 16.0, 11.0)
    ).reduce{ v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val eastAABB = Stream.of(
        box(15.0, 6.0, 0.0, 16.0, 7.0, 16.0),
        box(11.0, 0.0, 0.0, 15.0, 7.0, 16.0),
        box(0.0, 0.0, 0.0, 11.0, 16.0, 16.0)
    ).reduce{ v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val westAABB = Stream.of(
        box(0.0, 6.0, 0.0, 1.0, 7.0, 16.0),
        box(1.0, 0.0, 0.0, 5.0, 7.0, 16.0),
        box(5.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    ).reduce{ v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun onDestroyedByPlayer(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        willHarvest: Boolean,
        fluid: FluidState
    ): Boolean {
        val server = level.server ?: return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
        server.playerList.players.forEach { player ->
            warTimerMap[player]?.let {
                val increase = it.first.third + 30
                warTimerMap.put(player, Triple(it.first.first, 41, increase) to (it.second.first to true))
                PacketHandler.NETWORK.send(
                    PacketDistributor.PLAYER.with { player },
                    WarTimerIncrementPacket(true, increase)
                )
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.getShape(pState, pLevel, pPos, pContext)",
            "net.minecraft.world.level.block.Block"
        )
    )
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape = when (pState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
        Direction.SOUTH -> southAABB
        Direction.EAST -> eastAABB
        Direction.WEST -> westAABB
        else -> northAABB
    }
}