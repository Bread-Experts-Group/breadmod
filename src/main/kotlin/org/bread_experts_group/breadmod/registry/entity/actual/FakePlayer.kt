package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import java.util.Optional
import java.util.UUID

// todo needs work to be summonable.
class FakePlayer(
	type: EntityType<FakePlayer>,
	level: Level
) : LivingEntity(type, level) {
	constructor(
		level: Level,
		pos: BlockPos,
		owner: LivingEntity?
	) : this(ModEntityTypes.FAKE_PLAYER.get(), level) {
		this.setPos(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
		this.owner = owner
		this.entityData.set(Companion.ownerUUID, Optional.ofNullable(owner!!.uuid))
		this.entityData.set(Companion.ownerID, owner.id)
	}

	private var owner: LivingEntity? = null

	companion object {
		val ownerUUID: EntityDataAccessor<Optional<UUID>> =
			SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.OPTIONAL_UUID)
		val ownerID: EntityDataAccessor<Int> =
			SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.INT)
		val defaultUUID: UUID = UUID.fromString("880485d4-bea0-4689-b5cb-d4d419bcc923")

		fun createAttributes(): AttributeSupplier.Builder = createLivingAttributes()
	}

	fun getOwnerUUID(): UUID = this.entityData.get(Companion.ownerUUID).orElse(Companion.defaultUUID)
	override fun defineSynchedData(builder: SynchedEntityData.Builder) {
		super.defineSynchedData(builder)
		builder.define(Companion.ownerUUID, Optional.empty())
		builder.define(Companion.ownerID, 0)
	}

	override fun readAdditionalSaveData(compound: CompoundTag) {
		super.readAdditionalSaveData(compound)
		(this.owner ?: return).uuid = compound.getUUID("owner")
	}

	override fun getArmorSlots(): Iterable<ItemStack?> = emptySet()
	override fun getItemBySlot(slot: EquipmentSlot): ItemStack = ItemStack.EMPTY
	override fun setItemSlot(
		slot: EquipmentSlot,
		stack: ItemStack
	) {
	}

	override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
	override fun addAdditionalSaveData(compound: CompoundTag) {
		compound.putUUID("owner", this.getOwnerUUID())
//        owner?.let { compound.putUUID("owner", it.uuid) }
//        owner!!.uuid = compound.getUUID("owner")
	}

	override fun isInvulnerable(): Boolean = true
}