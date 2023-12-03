package breadmod.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

fun entityRaycast(entity: Entity, maxDistance: Boolean, includeFluids: Boolean) {
    val directionVector = Vec3.directionFromRotation(entity.rotationVector)
}