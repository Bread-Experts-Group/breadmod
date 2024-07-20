package breadmod.item.tool

import breadmod.datagen.tag.ModBlockTags
import breadmod.datagen.tag.ModItemTags
import breadmod.registry.block.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CarvedPumpkinBlock
import net.minecraft.world.level.block.state.BlockState

class KnifeItem(
    pTier: Tier,
    pAttackDamageModifier: Float,
    pAttackSpeedModifier: Float,
) : DiggerItem(pAttackDamageModifier, pAttackSpeedModifier, pTier, ModBlockTags.MINEABLE_WITH_KNIFE, Properties()) {
    override fun canAttackBlock(pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player): Boolean =
        !pPlayer.isCreative

    override fun hurtEnemy(pStack: ItemStack, pTarget: LivingEntity, pAttacker: LivingEntity): Boolean {
        pStack.hurtAndBreak(1, pAttacker) { user -> user.broadcastBreakEvent(EquipmentSlot.MAINHAND) }
        return true
    }

    override fun useOn(pContext: UseOnContext): InteractionResult {
        val level = pContext.level
        val handStack = pContext.itemInHand
        val clickedPos = pContext.clickedPos
        val blockState = level.getBlockState(clickedPos)
        val facing = pContext.clickedFace

        return if(blockState.block == Blocks.PUMPKIN && handStack.`is`(ModItemTags.KNIVES)) {
            val player = pContext.player
            if(player != null && !level.isClientSide) {
                val direction = if(facing.axis == Direction.Axis.Y) player.direction.opposite else facing
                level.playSound(null, clickedPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
                level.setBlockAndUpdate(clickedPos,
                    Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction))

                val itemEntity = createItemEntity(Items.PUMPKIN_SEEDS, 4, level, clickedPos, direction)
                itemEntity.setDeltaMovement(
                    0.05 * direction.stepX + level.random.nextDouble() * 0.02,
                    0.05,
                    0.05 * direction.stepZ + level.random.nextDouble() * 0.02
                )

                level.addFreshEntity(itemEntity)
                handStack.hurtAndBreak(1, player) { playerTool -> playerTool.broadcastBreakEvent(pContext.hand) }
            }

            InteractionResult.sidedSuccess(level.isClientSide)
        } else if(blockState.block == ModBlocks.BREAD_BLOCK.get().block && handStack.`is`(ModItemTags.KNIVES)) {
            val player = pContext.player
            if(player != null && !level.isClientSide) {
                val direction = if(facing.axis == Direction.Axis.Y) player.direction.opposite else facing
                level.playSound(null, clickedPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
                level.setBlockAndUpdate(clickedPos, Blocks.AIR.defaultBlockState())

                val itemEntity = createItemEntity(Items.BREAD, 9, level, clickedPos, direction)
                itemEntity.setDeltaMovement(
                    0.05 * direction.stepX + level.random.nextDouble() * 0.02,
                    0.05,
                    0.05 * direction.stepZ + level.random.nextDouble() * 0.02
                )

                level.addFreshEntity(itemEntity)
                handStack.hurtAndBreak(1, player) { playerTool -> playerTool.broadcastBreakEvent(pContext.hand) }
            }
            InteractionResult.sidedSuccess(level.isClientSide)
        } else InteractionResult.PASS
    }

    private fun createItemEntity(
        pItem: Item,
        pCount: Int,
        pLevel: Level,
        pPos: BlockPos,
        pDirection:
        Direction
    ): ItemEntity = ItemEntity(
        pLevel,
        pPos.x + 0.5 + pDirection.stepX * 0.65,
        pPos.y + 0.1,
        pPos.z + 0.5 + pDirection.stepZ * 0.65,
        ItemStack(pItem, pCount)
    )
}