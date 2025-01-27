package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType.SELF
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.VehicleEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager

class Forklift(entityType: EntityType<Forklift>, level: Level) : VehicleEntity(entityType, level) {
	override fun readAdditionalSaveData(compound: CompoundTag) {
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {
	}

	override fun getDropItem(): Item = Items.DIRT

	override fun getPassengerRidingPosition(entity: Entity): Vec3 =
		this.position().add(0.0, 0.3, 0.0)

	override fun canBeCollidedWith(): Boolean = false

	override fun tick() {
		super.tick()
		this.applyGravity()
		this.move(SELF, this.deltaMovement)
		val list = this.level().getEntities(this, this.boundingBox.inflate(0.1))
		list
			.asSequence()
			.filterNot { it.hasPassenger(this) }
			.forEach<Entity>(this::push)
	}

	override fun isPushable(): Boolean = true

	override fun interact(player: Player, hand: InteractionHand): InteractionResult {
		LogManager.getLogger().info("interact")
		return if (!this.level().isClientSide) {
			if (player.startRiding(this)) InteractionResult.CONSUME else InteractionResult.PASS
		} else InteractionResult.SUCCESS
	}

	override fun getDefaultGravity(): Double = 0.05
}