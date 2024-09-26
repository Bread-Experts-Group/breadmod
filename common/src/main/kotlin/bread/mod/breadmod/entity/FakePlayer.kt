package bread.mod.breadmod.entity

import bread.mod.breadmod.registry.entity.ModEntityTypes
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
import java.util.Optional
import java.util.UUID

class FakePlayer(
    type: EntityType<FakePlayer>,
    level: Level
) : LivingEntity(type, level) {
    var owner: LivingEntity? = null

    companion object {
        val ownerUUID: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.OPTIONAL_UUID)
        val ownerID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(FakePlayer::class.java, EntityDataSerializers.INT)

        fun createAttributes(): AttributeSupplier.Builder = createLivingAttributes()
    }

    constructor(
        level: Level,
        x: Double,
        y: Double,
        z: Double,
        owner: LivingEntity?
    ) : this(ModEntityTypes.FAKE_PLAYER.get(), level) {
        this.setPos(x, y, z)
        this.owner = owner
        entityData.set(ownerUUID, Optional.ofNullable(owner!!.uuid))
        entityData.set(ownerID, owner.id)
    }

    fun getOwnerUUID(): UUID = entityData.get(ownerUUID).orElse(null)

    fun getOwnerID(): Int = entityData.get(ownerID)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(ownerUUID, Optional.empty())
        builder.define(ownerID, 0)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        owner!!.uuid = compound.getUUID("owner")
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
        compound.putUUID("owner", getOwnerUUID())
//        owner?.let { compound.putUUID("owner", it.uuid) }
//        owner!!.uuid = compound.getUUID("owner")
    }

    override fun isInvulnerable(): Boolean = true

    /**
     * Gets called every tick from main Entity class
     */
    override fun baseTick() {
//        if (level().isClientSide) LogManager.getLogger().info(getOwnerUUID())
        super.baseTick()
    }
}