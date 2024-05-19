package breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level

class ExplosionExtended(val pLevel: Level, val pPosition: BlockPos, val pRange: Int, val pEntity: Entity? = null, val type: Explosion.BlockInteraction) {
}