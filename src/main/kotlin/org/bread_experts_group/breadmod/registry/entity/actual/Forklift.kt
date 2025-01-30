package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.CONSUME
import net.minecraft.world.InteractionResult.PASS
import net.minecraft.world.InteractionResult.SUCCESS
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType.SELF
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.VehicleEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes

class Forklift(entityType: EntityType<Forklift>, level: Level) : VehicleEntity(entityType, level) {
	constructor(
		level: Level,
		x: Double,
		y: Double,
		z: Double,
		rotation: Float
	) : this(ModEntityTypes.FORKLIFT.get(), level) {
		this.setPos(x, y, z)
		this.xo = x
		this.yo = y
		this.zo = z
		this.yRotO = rotation
	}

	constructor(level: Level, pos: BlockPos, rotation: Float = 0f) : this(
		level,
		pos.x.toDouble() + 0.5,
		pos.y.toDouble(),
		pos.z.toDouble() + 0.5,
		rotation
	)

	override fun readAdditionalSaveData(compound: CompoundTag) {
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {
	}

	override fun getDropItem(): Item = Items.DIRT

	override fun getPassengerRidingPosition(entity: Entity): Vec3 =
		this.position().add(0.0, 0.3, 0.0)

	override fun canBeCollidedWith(): Boolean = false

	// todo work out friction, maybe make a generic slowdown method to ease back into 0 delta?
	override fun tick() {
		super.tick()
		this.applyGravity()
		this.deltaMovement = this.deltaMovement.multiply(0.80, 0.0, 0.80)
		this.move(SELF, this.deltaMovement)
		val list = this.level().getEntities(this, this.boundingBox.inflate(0.1))
		list.asSequence().filterNot { it.hasPassenger(this) }.forEach<Entity>(this::push)
	}

	override fun isPushable(): Boolean = true

	override fun interact(player: Player, hand: InteractionHand): InteractionResult {
		val result = super.interact(player, hand)
		if (result.consumesAction()) return result
		return if (player.isSecondaryUseActive) PASS
		else if (this.isVehicle) PASS
		else if (!this.level().isClientSide) {
			if (player.startRiding(this)) CONSUME else PASS
		} else SUCCESS
	}

	override fun getDefaultGravity(): Double = 0.05
}