package breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

@Suppress("SpellCheckingInspection")
class BreadBlock : Block(Properties.copy(Blocks.HAY_BLOCK).strength(0.5f).ignitedByLava()) {

    override fun isFlammable(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Boolean {
        return true
    }

    override fun getFlammability(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int {
        return 15
    }

    override fun getFireSpreadSpeed(
        state: BlockState?,
        level: BlockGetter?,
        pos: BlockPos?,
        direction: Direction?
    ): Int {
        return 30
    }

    override fun onCaughtFire(
        state: BlockState?,
        pLevel: Level,
        pPos: BlockPos,
        direction: Direction?,
        igniter: LivingEntity?
    ) {
//        val vec3 = pPos?.let { Vec3.atLowerCornerWithOffset(it, 0.5, 1.01, 0.5).offsetRandom(pLevel?.random ?: RandomSource.create(1), 0.7f) }
//        val itementity = pLevel?.let { vec3?.let { it1 -> ItemEntity(it, it1.x(), vec3.y(), vec3.z(), ItemStack(Items.CHARCOAL)) } }
//        itementity!!.setDefaultPickUpDelay()
//        itementity.isInvulnerable
//        pLevel.addFreshEntity(itementity)

        pLevel.setBlock(pPos, Blocks.COAL_BLOCK.defaultBlockState(), 1 or 2)

        super.onCaughtFire(state, pLevel, pPos, direction, igniter)
    }

}