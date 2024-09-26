package bread.mod.breadmod.block

import bread.mod.breadmod.entity.PrimedHappyBlock
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.plus
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEvent.Context
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class HappyBlock : TntBlock(Properties.ofFullCopy(Blocks.TNT)) {
    private fun BlockPos.adjust() = Vec3.atLowerCornerOf(this).plus(Vec3(0.5, 0.0, 0.5))

    override fun wasExploded(level: Level, pos: BlockPos, explosion: Explosion) {
        if (!level.isClientSide) {
            val primedHappyBlock =
                PrimedHappyBlock(level, pos.adjust(), owner = explosion.indirectSourceEntity, shouldSpread = true)
            val fuse = primedHappyBlock.fuse
            primedHappyBlock.fuse = (level.random.nextInt(fuse / 4) + fuse / 8)
            level.addFreshEntity(primedHappyBlock)
        }
    }

    // having to make my own explosion function because the superclass function is static :angry:
    private fun explodeHappyBlock(
        level: Level,
        pos: BlockPos,
        entity: LivingEntity?
    ) {
        if (!level.isClientSide) {
            val primedHappyBlock = PrimedHappyBlock(level, pos.adjust(), owner = entity, shouldSpread = true)
            level.addFreshEntity(primedHappyBlock)
            level.playSound(
                null,
                primedHappyBlock.x,
                primedHappyBlock.y,
                primedHappyBlock.z,
                ModSounds.HAPPY_BLOCK_FUSE.get(),
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            )
            level.gameEvent(entity, GameEvent.PRIME_FUSE, pos)
        }
    }

    private fun explodeHappyBlock(level: Level, pos: BlockPos) = explodeHappyBlock(level, pos, null)

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        if (level.hasNeighborSignal(pos)) {
            explodeHappyBlock(level, pos)
            level.removeBlock(pos, false)
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide && !player.isCreative && state.getValue(UNSTABLE)) {
            explodeHappyBlock(level, pos)
        }

        spawnDestroyParticles(level, player, pos, state)
        if (state.`is`(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.angerNearbyPiglins(player, false)
        }

        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(player, state))
        return state
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult =
        if (!stack.`is`(Items.FLINT_AND_STEEL) && !stack.`is`(Items.FIRE_CHARGE)) {
            ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        } else {
            explodeHappyBlock(level, pos, player)
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
            val item = stack.item
            if (stack.`is`(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand))
            } else {
                stack.consume(1, player)
            }

            player.awardStat(Stats.ITEM_USED.get(item))
            ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (!level.isClientSide) {
            val blockPos = hit.blockPos
            val entity = projectile.owner
            if (projectile.isOnFire && projectile.mayInteract(level, blockPos)) {
                explodeHappyBlock(level, blockPos, entity as? LivingEntity)
                level.removeBlock(blockPos, false)
            }
        }
    }
}