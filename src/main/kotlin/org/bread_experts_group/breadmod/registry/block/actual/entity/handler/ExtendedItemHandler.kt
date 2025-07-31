package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.items.IItemHandler
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.ITEM_ID_DESERIALIZER
import org.bread_experts_group.breadmod.network.BreadModCodecs.ITEM_ID_SERIALIZER
import org.bread_experts_group.breadmod.network.BreadModCodecs.compose
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedItemHandler.Slot.Companion.SLOT_ITEM_ID_SERIALIZER
import org.bread_experts_group.breadmod.registry.component.ModDataComponents.SLOTS
import org.bread_experts_group.breadmod.util.Color.DARK_GRAY
import org.bread_experts_group.breadmod.util.Color.LIGHT_GRAY
import org.bread_experts_group.breadmod.util.Color.SAFFRON
import org.bread_experts_group.breadmod.util.Color.component
import org.bread_experts_group.breadmod.util.floatRoundEven
import org.bread_experts_group.breadmod.util.int
import org.bread_experts_group.breadmod.util.percentRoundEven
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class ExtendedItemHandler(
	vararg slots: Slot
) : ParentedHandler<BreadModBlockEntity>, IItemHandler, DataComponentSerializable, INBTSerializable<Tag> {
	override lateinit var parent: BreadModBlockEntity
	val slots: MutableMap<Int, Slot> = mutableMapOf(*slots.mapIndexed { index, slot -> index to slot }.toTypedArray())

	fun dropContents(pos: BlockPos, level: Level) {
		// TODO: BigDecimal drop container
	}

	override fun getSlots(): Int = this.slots.size
	override fun getStackInSlot(slot: Int): ItemStack = this.slots[slot]?.itemStack() ?: ItemStack.EMPTY

	data class BigItemTransaction(
		val item: Item,
		val amount: BigDecimal,
		val components: DataComponentMap
	)

	fun bigInsertItem(
		slot: Int,
		transaction: BigItemTransaction,
		simulate: Boolean
	): BigItemTransaction {
		if (transaction.amount == BigDecimal.ZERO) return transaction
		val slot = this.slots[slot] ?: return transaction
		if (!slot.validity(transaction.item, transaction.amount, transaction.components)) return transaction
		val transfer = minOf(transaction.amount, slot.capacity - slot.amount)
		if (slot.amount == BigDecimal.ZERO) {
			if (!simulate) {
				slot.item = transaction.item
				slot.amount = transfer
				slot.components = transaction.components
				this.stateUpdated()
			}
			return transaction.copy(amount = transaction.amount - transfer)
		} else if (slot.item != transaction.item) return transaction
		val remainder = transaction.copy(amount = transaction.amount - transfer)
		if (!simulate) {
			slot.amount += transfer
			this.stateUpdated()
		}
		return remainder
	}

	override fun insertItem(
		slot: Int,
		stack: ItemStack,
		simulate: Boolean
	): ItemStack {
		val transaction = this.bigInsertItem(
			slot,
			BigItemTransaction(stack.item, BigDecimal(stack.count), stack.components),
			simulate
		)
		val stack = ItemStack(transaction.item, transaction.amount.int)
		stack.applyComponents(transaction.components)
		return stack
	}

	override fun extractItem(
		slot: Int,
		amount: Int,
		simulate: Boolean
	): ItemStack {
		println("delta")
		return ItemStack.EMPTY
	}

	override fun getSlotLimit(slot: Int): Int = this.slots[slot]?.capacity?.int ?: 0
	override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
		this.slots[slot]?.validity?.invoke(stack.item, BigDecimal(stack.count), stack.components) ?: true

	val big100: BigDecimal = BigDecimal.valueOf(100)
	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		this.slots.forEach { (index, slot) ->
			val component = '#'.component(DARK_GRAY)
			component.append(index.toString().component(LIGHT_GRAY))
			component.append(':'.component(DARK_GRAY))
			component.append(" ${slot.amount} ".component(SAFFRON))
			component.append('|'.component(DARK_GRAY))
			component.append(" ${slot.capacity} ".component(SAFFRON))
			component.append('('.component(DARK_GRAY))
			val percentage = slot.amount
				.divide(slot.capacity, percentRoundEven)
				.multiply(this.big100)
				.setScale(2, RoundingMode.HALF_EVEN)
			component.append(percentage.toString().component(SAFFRON))
			component.append('%'.component(LIGHT_GRAY))
			component.append(") ".component(DARK_GRAY))
			component.append('['.component(DARK_GRAY))
			component.append(slot.item.description.copy().withColor(SAFFRON))
			component.append(']'.component(DARK_GRAY))
			tooltipComponents.add(component)
		}
	}

	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		val slots = mutableListOf<Slot>()
		this.slots.forEach { slots.add(it.key, it.value) }
		map.set(SLOTS, slots)
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		val slots = from.get(SLOTS)
		if (slots != null) {
			this.slots.clear()
			slots.forEachIndexed { index, slot -> this.slots[index] = slot }
			this.stateUpdated()
		}
	}

	override fun serializeNBT(provider: HolderLookup.Provider): Tag {
		val list = ListTag()
		this.slots.forEach { (_, slot) ->
			val compound = CompoundTag()
			compound.putString("capacity", slot.capacity.toString())
			if (slot.amount > BigDecimal.ZERO) {
				compound.putString("amount", slot.amount.toString())
				compound.putString("item", SLOT_ITEM_ID_SERIALIZER(slot))
				val result = DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, slot.components)
				result.resultOrPartial().ifPresent {
					compound.put("components", it)
				}
			}
			list.add(compound)
		}
		return list
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: Tag) {
		if (nbt !is ListTag) return
		nbt.forEachIndexed { index, tag ->
			if (tag !is CompoundTag) return@forEachIndexed
			try {
				val capacity = BigDecimal(tag.getString("capacity"))
				val slot = this.slots.getOrPut(index) { Slot(capacity) }
				slot.item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("item")))
				slot.amount = BigDecimal(tag.getString("amount"))
				val result = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, tag.get("components"))
				result.resultOrPartial().ifPresent {
					slot.components = it.first
				}
			} catch (_: Exception) {
			}
		}
		if (nbt.isNotEmpty()) this.stateUpdated()
	}

	data class Slot(
		var capacity: BigDecimal
	) {
		var validity: (Item, BigDecimal, DataComponentMap) -> Boolean = { _, _, _ -> true }
		var amount: BigDecimal = BigDecimal.ZERO
		var item: Item = Items.AIR
		var components: DataComponentMap = DataComponentMap.EMPTY
		fun itemStack(): ItemStack {
			val percent = this.amount.divide(this.capacity, floatRoundEven).toFloat()
			val stack = ItemStack(this.item, (Item.ABSOLUTE_MAX_STACK_SIZE * percent).roundToInt())
			stack.applyComponents(this.components)
			return stack
		}

		companion object {
			val SLOT_ITEM_ID_SERIALIZER: (Slot) -> String = ITEM_ID_SERIALIZER.compose(Slot::item)
			val SLOT_DESERIALIZER: (BigDecimal, BigDecimal, String) -> Slot = { capacity, amount, itemID ->
				val slot = Slot(capacity)
				slot.item = ITEM_ID_DESERIALIZER(itemID)
				slot.amount = amount
				slot
			}
			val CODEC: Codec<Slot> = RecordCodecBuilder.create { inst ->
				inst.group(
					BIG_DECIMAL_CODEC.fieldOf("capacity").forGetter(Slot::capacity),
					BIG_DECIMAL_CODEC.fieldOf("amount").forGetter(Slot::amount),
					Codec.STRING.fieldOf("item_id").forGetter(this.SLOT_ITEM_ID_SERIALIZER),
				).apply(inst, this.SLOT_DESERIALIZER)
			}
			val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Slot> = StreamCodec.composite(
				BIG_DECIMAL_STREAM_CODEC, Slot::capacity,
				BIG_DECIMAL_STREAM_CODEC, Slot::amount,
				ByteBufCodecs.STRING_UTF8, this.SLOT_ITEM_ID_SERIALIZER,
				this.SLOT_DESERIALIZER
			)
		}
	}
}