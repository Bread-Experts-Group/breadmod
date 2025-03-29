package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
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
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.entity.PartEntity
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import kotlin.math.cos
import kotlin.math.sin

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
		this.yRot = rotation
	}

	constructor(level: Level, pos: BlockPos, rotation: Float = 0f) : this(
		level,
		pos.x.toDouble() + 0.5,
		pos.y.toDouble(),
		pos.z.toDouble() + 0.5,
		rotation
	)

	override fun makeBoundingBox(): AABB {
		return super.makeBoundingBox()
	}

	private val parts: Array<PartEntity<Forklift>> = arrayOf(
		ForkliftPart(this, Vec3(0.0, 3.0, 0.0), 0.5f, 0.5f, Vec3(1.0, 1.0, 1.0))
	)

	override fun readAdditionalSaveData(compound: CompoundTag) {
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {
	}

	override fun getDropItem(): Item = Items.DIRT

	override fun getParts(): Array<PartEntity<Forklift>> = this.parts

	override fun playStepSound(pos: BlockPos, state: BlockState) {
	}

	override fun getPassengerRidingPosition(entity: Entity): Vec3 =
		this.position().add(0.0, 0.3, 0.0)

	override fun shouldShowName(): Boolean = true

	override fun hasCustomName(): Boolean = true

	override fun getCustomName(): Component = Component.literal("FORKLIFT")

	override fun canBeCollidedWith(): Boolean = true

	override fun isMultipartEntity(): Boolean = true

	override fun setId(id: Int) {
		super.setId(id)
		for (i: Int in this.parts.indices) {
			this.parts[i].id = id + i + 1
		}
	}

	override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
		super.recreateFromPacket(packet)

		for (i: Int in this.parts.indices) {
			this.parts[i].id = i + packet.id
		}
	}

	private val sideFriction = 0.99
	private val forwardFriction = 0.01

	override fun tick() {
		super.tick()
		val radians = Math.toRadians(this.yRotO.toDouble())
		val forward = Vec3(cos(radians), 0.0, sin(radians))
		val right = Vec3(-forward.z, 0.0, forward.x)
		val forwardComponent = forward.scale(this.deltaMovement.dot(forward) * this.forwardFriction)
		val sideComponent = right.scale(this.deltaMovement.dot(right) * this.sideFriction)
		val combined = forwardComponent.add(sideComponent)
		this.deltaMovement = Vec3(combined.x, this.deltaMovement.y - 0.05, combined.z)
		this.move(SELF, this.deltaMovement)
		this.applyGravity()
		this.parts.forEach(PartEntity<Forklift>::tick)
		this.level().getEntities(
			this,
			this.boundingBox.inflate(0.1)
		).forEach(this::push)
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
}