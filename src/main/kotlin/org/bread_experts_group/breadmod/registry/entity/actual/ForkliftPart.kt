package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.SynchedEntityData.Builder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.entity.PartEntity

class ForkliftPart(
	parent: Forklift,
	private var pos: Vec3,
	width: Float,
	height: Float,
	collisionBox: Vec3
) : PartEntity<Forklift>(parent) {
	private val size: EntityDimensions = EntityDimensions.scalable(width, height)

	init {
		this.size.makeBoundingBox(collisionBox)
	}

	override fun tick() {
		super.tick()
		this.moveTo(this.parent.position().add(this.pos))
		this.yRot = this.parent.yRot
	}

	override fun getDimensions(pose: Pose): EntityDimensions = this.size

	override fun shouldShowName(): Boolean = true

	override fun hasCustomName(): Boolean = true

	override fun getCustomName(): Component = Component.literal("FORKLIFT PART")

	override fun position(): Vec3 = this.parent.position().add(this.pos)

	override fun `is`(entity: Entity): Boolean = this.parent == entity

	override fun shouldBeSaved(): Boolean = false

	override fun canBeCollidedWith(): Boolean = true

	override fun getPickResult(): ItemStack? = this.parent.pickResult

	override fun readAdditionalSaveData(compound: CompoundTag) {
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {
	}

	override fun defineSynchedData(builder: Builder) {
	}
}