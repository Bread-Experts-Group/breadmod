package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.stats.Stats
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.EventHooks
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.entity.ModEntityDataSerializers.BIG_DESCRIPTOR_ITEM
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.itemStack
import org.bread_experts_group.breadmod.util.int
import java.math.BigDecimal
import kotlin.math.min

class BigItemContainer(
	level: Level,
	descriptors: List<BigDescriptor<Item>> = emptyList()
) : ItemEntity(ModEntityTypes.BIG_ITEM_CONTAINER.get(), level) {
	companion object {
		@JvmStatic
		val BIG_DATA_ITEM: EntityDataAccessor<List<BigDescriptor<Item>>> = SynchedEntityData.defineId(
			BigItemContainer::class.java,
			BIG_DESCRIPTOR_ITEM.get()
		)
	}

	var descriptors: List<BigDescriptor<Item>>
		get() = this.entityData.get(Companion.BIG_DATA_ITEM)
		set(value) {
			this.entityData.set(Companion.BIG_DATA_ITEM, value)
		}
	val stack: ItemStack
		get() {
			var count = 0
			for (descriptor in this.descriptors) {
				count += descriptor.amount.int
				if (count > Item.ABSOLUTE_MAX_STACK_SIZE) break
			}
			return ModBlocks.BREAD_BLOCK.toStack(min(count, Item.ABSOLUTE_MAX_STACK_SIZE))
		}

	init {
		this.descriptors = descriptors
		this.setNeverPickUp()
		this.setExtendedLifetime()
	}

	override fun getItem(): ItemStack = this.stack
	override fun defineSynchedData(builder: SynchedEntityData.Builder) {
		super.defineSynchedData(builder)
		builder.define(Companion.BIG_DATA_ITEM, emptyList())
	}

	override fun addAdditionalSaveData(compound: CompoundTag) {
		BreadModCodecs.BIG_DESCRIPTOR_ITEM_CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.descriptors).ifSuccess {
			compound.put("BigItems", it)
		}
		super.addAdditionalSaveData(compound)
	}

	override fun readAdditionalSaveData(compound: CompoundTag) {
		val items = compound.get("BigItems") ?: return
		BreadModCodecs.BIG_DESCRIPTOR_ITEM_CODEC.listOf().decode(NbtOps.INSTANCE, items).ifSuccess {
			this.descriptors = it.first
		}
		super.readAdditionalSaveData(compound)
	}

	override fun playerTouch(entity: Player) {
		if (entity.level().isClientSide) return
		if (EventHooks.fireItemPickupPre(this, entity).canPickup().isFalse) return
		if (this.target != null && this.target != entity.getUUID()) return
		val mutableDescriptors = this.descriptors.toMutableList()
		if (mutableDescriptors.isEmpty()) {
			this.discard()
			return
		}
		val immediate = mutableDescriptors[0]
		val immediateStack = immediate.itemStack()
		val countBeforeAdd = immediateStack.count
		entity.inventory.add(immediateStack)
		val delta = countBeforeAdd - immediateStack.count
		val newDescriptor = BigDescriptor(
			immediate.amount - BigDecimal(delta),
			immediate.value, immediate.components
		)
		if (delta > 0) {
			entity.take(this, delta)
			entity.awardStat(Stats.ITEM_PICKED_UP.get(immediate.value), delta)
			entity.onItemPickup(this)
		}
		if (newDescriptor.amount <= BigDecimal.ZERO) {
			mutableDescriptors.removeFirst()
			if (mutableDescriptors.isEmpty()) {
				this.discard()
				return
			}
		}
		mutableDescriptors[0] = newDescriptor
		this.descriptors = mutableDescriptors
	}
}