package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.numeric.geometry.point.Point3

fun BlockPos.toPoint3(): Point3<Int> = Point3(this.x, this.y, this.z)
fun Point3<Int>.toBlockPos(): BlockPos = BlockPos(this.x, this.y, this.z)
fun Point3<Int>.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())