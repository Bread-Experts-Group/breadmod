package bread.mod.breadmod.block

import bread.mod.breadmod.block.entity.ToasterBlockEntity
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.item.ModItems
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class ToasterBlock : BaseEntityBlock(Properties.of()) {
    val codec: MapCodec<ToasterBlock> = simpleCodec { ToasterBlock() }

    override fun codec(): MapCodec<out BaseEntityBlock> = codec

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val entity = (level.getBlockEntity(pos) as? ToasterBlockEntity) ?: return ItemInteractionResult.FAIL
        entity.setItem(0, ModItems.BREAD_SLICE.get().defaultInstance.copyWithCount(2))
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity = ToasterBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        ModBlockEntityTypes.TOASTER.get()
    ) { tLevel: Level, tPos: BlockPos, tState: BlockState, tBlockEntity: ToasterBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity)
    }
}