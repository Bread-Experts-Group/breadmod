package bread.mod.breadmod.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.command.server.WarTimerCommand.increaseTime
import bread.mod.breadmod.registry.CommonEventRegistry.warTimerMap
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.stream.Stream

class WarTerminalBlock : Block(Properties.of()) {
    val northAABB: VoxelShape = Stream.of(
        box(0.0, 6.0, 0.0, 16.0, 7.0, 1.0),
        box(0.0, 0.0, 1.0, 16.0, 7.0, 5.0),
        box(0.0, 0.0, 5.0, 16.0, 16.0, 16.0)
    ).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val southAABB: VoxelShape = Stream.of(
        box(0.0, 6.0, 15.0, 16.0, 7.0, 16.0),
        box(0.0, 0.0, 11.0, 16.0, 7.0, 15.0),
        box(0.0, 0.0, 0.0, 16.0, 16.0, 11.0)
    ).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val eastAABB: VoxelShape = Stream.of(
        box(15.0, 6.0, 0.0, 16.0, 7.0, 16.0),
        box(11.0, 0.0, 0.0, 15.0, 7.0, 16.0),
        box(0.0, 0.0, 0.0, 11.0, 16.0, 16.0)
    ).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    val westAABB: VoxelShape = Stream.of(
        box(0.0, 6.0, 0.0, 1.0, 7.0, 16.0),
        box(1.0, 0.0, 0.0, 5.0, 7.0, 16.0),
        box(5.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    ).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, thisPlayer: Player): BlockState {
        val server = level.server ?: return super.playerWillDestroy(level, pos, state, thisPlayer)
        server.playerList.players.forEach { player ->
            val check = warTimerMap[player]
            if (check != null) increaseTime(player, check, 30)
        }
        return super.playerWillDestroy(level, pos, state, thisPlayer)
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

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            ModMainCommon.modTranslatable("block", "war_terminal", "tooltip").withStyle(ChatFormatting.RED)
        )
    }
}