package bread.mod.breadmod.item.tool

import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.tag.BlockTags.MINEABLE_WITH_KNIFE
import bread.mod.breadmod.registry.tag.ItemTags.KNIVES
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
    tier: Tier,
) : DiggerItem(tier, MINEABLE_WITH_KNIFE, Properties()) {
    override fun canAttackBlock(state: BlockState, level: Level, pos: BlockPos, player: Player): Boolean =
        !player.isCreative

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND)
        return true
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val handStack = context.itemInHand
        val clickedPos = context.clickedPos
        val blockState = level.getBlockState(clickedPos)
        val facing = context.clickedFace

        return if (blockState.block == Blocks.PUMPKIN && handStack.`is`(KNIVES)) {
            val player = context.player
            if (player != null && !level.isClientSide) {
                val direction = if (facing.axis == Direction.Axis.Y) player.direction.opposite else facing
                level.playSound(null, clickedPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
                level.setBlockAndUpdate(
                    clickedPos,
                    Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction)
                )

                val itemEntity = createItemEntity(Items.PUMPKIN_SEEDS, 4, level, clickedPos, direction)
                itemEntity.setDeltaMovement(
                    0.05 * direction.stepX + level.random.nextDouble() * 0.02,
                    0.05,
                    0.05 * direction.stepZ + level.random.nextDouble() * 0.02
                )

                level.addFreshEntity(itemEntity)
                handStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
            }

            InteractionResult.sidedSuccess(level.isClientSide)
        } else if (blockState.block == ModBlocks.BREAD_BLOCK.get().block && handStack.`is`(KNIVES)) {
            val player = context.player
            if (player != null && !level.isClientSide) {
                val direction = if (facing.axis == Direction.Axis.Y) player.direction.opposite else facing
                level.playSound(null, clickedPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
                level.setBlockAndUpdate(clickedPos, Blocks.AIR.defaultBlockState())

                val itemEntity = createItemEntity(Items.BREAD, 9, level, clickedPos, direction)
                itemEntity.setDeltaMovement(
                    0.05 * direction.stepX + level.random.nextDouble() * 0.02,
                    0.05,
                    0.05 * direction.stepZ + level.random.nextDouble() * 0.02
                )

                level.addFreshEntity(itemEntity)
                handStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
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