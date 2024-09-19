package bread.mod.breadmod.block

import bread.mod.breadmod.util.plus
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.phys.Vec3

// todo reimplement features
class HappyBlock : TntBlock(Properties.ofFullCopy(Blocks.TNT)) {
    private fun BlockPos.adjust() = Vec3.atLowerCornerOf(this).plus(Vec3(0.5, 0.0, 0.5))

    override fun wasExploded(level: Level, pos: BlockPos, explosion: Explosion) {
        super.wasExploded(level, pos, explosion)
    }
}