package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.SynchedEntityData.Builder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.registry.entity.ModEntityDataSerializers
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3f

class Rocket(
	entityType: EntityType<Rocket>,
	level: Level
) : Entity(entityType, level) {
	constructor(level: Level, posA: BlockPos, posB: BlockPos) : this(ModEntityTypes.ROCKET.get(), level) {
		val aabb = AABB.encapsulatingFullBlocks(posA, posB)
		val map: Map<BlockPos, BlockState> = buildMap {
			BlockPos.betweenClosedStream(aabb).forEach { pos ->
				val immutable = pos.immutable()
				this[immutable] = level.getBlockState(immutable)
			}
		}
		this.entityData.set(Companion.BLOCKS, map)
		this.entityData.set(Companion.ORIGIN, posA)
		this.entityData.set(Companion.WIDTH, (aabb.maxX - aabb.minX).toFloat())
		this.entityData.set(Companion.HEIGHT, (aabb.maxY - aabb.minY).toFloat())
		val scaled = posA.div(2) - posB.div(2)
		this.entityData.set(Companion.BOTTOM_CENTER, scaled.toVector3f().add(0f, scaled.y.toFloat(), 0f))
		LogManager.getLogger().info(aabb)
	}

	override fun tick() {
		this.move(MoverType.SELF, Vec3(0.0, 0.1, 0.0))
		this.level().getEntities(this, this.boundingBox).forEach(this::push)
	}

	override fun makeBoundingBox(): AABB {
		val width = this.entityData.get(Companion.WIDTH) / 2
		val height = this.entityData.get(Companion.HEIGHT)
		val pos = this.position()
		return AABB(pos.x - width, pos.y, pos.z - width, pos.x + width, pos.y + height, pos.z + width)
	}

	companion object {
		val BLOCKS: EntityDataAccessor<Map<BlockPos, BlockState>> =
			SynchedEntityData.defineId(Rocket::class.java, ModEntityDataSerializers.BLOCK_MAP.get())
		val ORIGIN: EntityDataAccessor<BlockPos> =
			SynchedEntityData.defineId(Rocket::class.java, EntityDataSerializers.BLOCK_POS)
		val WIDTH: EntityDataAccessor<Float> =
			SynchedEntityData.defineId(Rocket::class.java, EntityDataSerializers.FLOAT)
		val HEIGHT: EntityDataAccessor<Float> =
			SynchedEntityData.defineId(Rocket::class.java, EntityDataSerializers.FLOAT)
		val BOTTOM_CENTER: EntityDataAccessor<Vector3f> =
			SynchedEntityData.defineId(Rocket::class.java, EntityDataSerializers.VECTOR3)
	}

	override fun defineSynchedData(builder: Builder) {
		builder.define(Companion.BLOCKS, emptyMap())
		builder.define(Companion.ORIGIN, BlockPos.ZERO)
		builder.define(Companion.WIDTH, 0f)
		builder.define(Companion.HEIGHT, 0f)
		builder.define(Companion.BOTTOM_CENTER, Vector3f())
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {}
	override fun readAdditionalSaveData(compound: CompoundTag) {}
}