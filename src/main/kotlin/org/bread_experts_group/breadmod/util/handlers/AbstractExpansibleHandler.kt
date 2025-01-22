package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag
import java.math.BigDecimal

abstract class AbstractExpansibleHandler<T : HandlerSerializable> : HandlerLimits, HandlerListener {
	protected open val units: MutableList<T> = mutableListOf()
	override var receiveAction: ListenerHandler = { _, _, _, _ -> null }
	override var extractAction: ListenerHandler = { _, _, _, _ -> null }
	override var amount: BigDecimal
		get() = this.units.sumOf { it.amount }
		set(_) = throw UnsupportedOperationException("Amount is provisional for ExpansibleHandlers, see units")
	override var capacity: BigDecimal?
		get() = if (this.units.any { it.capacity == null }) null else this.units.sumOf { it.capacity!! }
		set(_) = throw UnsupportedOperationException("Capacity is provisional for ExpansibleHandlers, see units")
	override var maxIn: BigDecimal?
		get() = if (this.units.any { it.maxIn == null }) null else this.units.sumOf { it.maxIn!! }
		set(_) = throw UnsupportedOperationException("Max input is provisional for ExpansibleHandlers, see units")
	override var maxOut: BigDecimal?
		get() = if (this.units.any { it.maxOut == null }) null else this.units.sumOf { it.maxOut!! }
		set(_) = throw UnsupportedOperationException("Max output is provisional for ExpansibleHandlers, see units")

	fun getUnit(unit: Int): T = this.units[unit]
	fun getUnits(): Int = this.units.size

	override fun fillDecimal(
		count: BigDecimal,
		simulate: Boolean,
		additional: MutableList<Any>
	): Pair<BigDecimal, List<Any>> {
		var actualCount = count
		for (unitIndex in this.units.indices) {
			val unit = this.units[unitIndex]
			if (unit.maxIn == BigDecimal.ZERO) continue
			val toFill = this.receiveAction(actualCount, simulate, unitIndex, additional) ?: actualCount
			if (toFill == BigDecimal.ZERO) continue
			val filled = unit.fillDecimal(
				unit.maxIn?.min(toFill) ?: toFill,
				simulate,
				additional
			).first
			actualCount -= filled
			if (actualCount == BigDecimal.ZERO) break
		}
		return count - actualCount to additional
	}

	override fun drainDecimal(
		count: BigDecimal,
		simulate: Boolean,
		additional: MutableList<Any>
	): Pair<BigDecimal, List<Any>> {
		var actualCount = count
		for (unitIndex in this.units.indices) {
			val unit = this.units[unitIndex]
			if (unit.maxOut == BigDecimal.ZERO) continue
			val toDrain = this.extractAction(actualCount, simulate, unitIndex, additional) ?: actualCount
			if (toDrain == BigDecimal.ZERO) continue
			val drained = unit.drainDecimal(
				unit.maxOut?.min(toDrain) ?: toDrain,
				simulate,
				additional
			).first
			actualCount -= drained
			if (actualCount == BigDecimal.ZERO) break
		}
		return count - actualCount to additional
	}

	fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
		this.units.forEachIndexed { index, unit ->
			tag.put("$index", CompoundTag().also { unitTag ->
				unitTag.put("additional", unit.serializeNBT())
				unitTag.putString("amount", unit.amount.toEngineeringString())
				unit.capacity?.let { unitTag.putString("capacity", it.toEngineeringString()) }
			})
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			val unit = this.units[it.toInt()]
			val thisCompound = from.getCompound(it)
			unit.deserializeNBT(thisCompound.getCompound("additional"))
			unit.capacity =
				if (thisCompound.contains("capacity")) BigDecimal(thisCompound.getString("capacity"))
				else null
			unit.amount = BigDecimal(thisCompound.getString("amount"))
		}
	}
}