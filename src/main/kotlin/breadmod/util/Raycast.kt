package breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i

fun Entity.raycast(maxDistance: Int, includeFluids: Boolean): HitResult {
    val directionVector = Vec3.directionFromRotation(this.rotationVector)

    var lastVec3 = eyePosition
    var lastBlockPos = blockPosition()

    repeat(maxDistance) {
        lastVec3 = lastVec3.add(directionVector.scale(it.toDouble()))
        lastBlockPos = BlockPos(lastVec3.toVec3i())

        val block = this.level().getBlockState(lastBlockPos)
        if((block.fluidState.isSource && includeFluids) || (!block.fluidState.isSource && !block.isAir))
            return BlockHitResult(lastVec3, direction, lastBlockPos, true)

        val foundEntity = this.level().getEntities(this, AABB.ofSize(lastVec3, 1.0, 1.0, 1.0)).firstOrNull()
        if(foundEntity != null) return EntityHitResult(foundEntity, lastVec3)
    }

    return BlockHitResult.miss(lastVec3, direction, lastBlockPos)
}