package org.bread_experts_group.breadmod.registry.entity.actual

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
import java.util.*

// todo needs work to be summonable.
class FakePlayer(
	type: EntityType<FakePlayer>,
	level: Level
) : LivingEntity(type, level) {
	private var owner: LivingEntity? = null

	companion object {
		val ownerUUID: EntityDataAccessor<Optional<UUID>> =
			SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.OPTIONAL_UUID)
		val ownerID: EntityDataAccessor<Int> =
			SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.INT)

		fun createAttributes(): AttributeSupplier.Builder = createLivingAttributes()
	}

	fun getOwnerUUID(): UUID = this.entityData.get(Companion.ownerUUID).orElse(null)
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