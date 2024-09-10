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
        val player = context.player ?: return InteractionResult.FAIL

        val level = context.level
        val handStack = context.itemInHand
        val clickedPos = context.clickedPos
        val blockState = level.getBlockState(clickedPos)
        val facing = context.clickedFace

        fun getDirectionAndCarve(): Direction {
            level.playSound(null, clickedPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
            return if (facing.axis == Direction.Axis.Y) player.direction.opposite else facing
        }

        fun ItemEntity.spawnInWorld() {
            // There is probably a utility function for this
            this.setDeltaMovement(
                0.05 * direction.stepX + getRandomX(0.02),
                0.05,
                0.05 * direction.stepZ + getRandomZ(0.02)
            )
            this.setPickUpDelay(20)
            level.addFreshEntity(this)
        }

        return if (!level.isClientSide && handStack.`is`(KNIVES)) {
            when (blockState.block) {
                Blocks.PUMPKIN -> {
                    val direction = getDirectionAndCarve()
                    level.setBlockAndUpdate(
                        clickedPos,
                        Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction)
                    )
                }

                ModBlocks.BREAD_BLOCK.get().block -> {
                    val direction = getDirectionAndCarve()
                    level.setBlockAndUpdate(clickedPos, Blocks.AIR.defaultBlockState())

                    createItemEntity(Items.BREAD, 9, level, clickedPos, direction).spawnInWorld()
                    handStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
                }

                else -> return InteractionResult.PASS
            }

            InteractionResult.SUCCESS
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