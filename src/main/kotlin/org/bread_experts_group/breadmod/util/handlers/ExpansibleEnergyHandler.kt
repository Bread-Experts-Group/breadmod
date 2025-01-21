package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.util.capInt
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler.ExpansibleEnergyHolder
import java.math.BigDecimal
import kotlin.reflect.full.isSubclassOf

open class ExpansibleEnergyHandler(
	cells: List<ExpansibleCell>,
	var receiveAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? = { _, _, _ -> null },
	var extractAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? = { _, _, _ -> null },
) : IEnergyStorage, ExpansibleEnergyHolder {
	init {
		val stackTrace = Thread.currentThread().stackTrace
		val callingLocation = this::class.java.classLoader.loadClass(stackTrace.first {
			it.className != this::class.qualifiedName && !it.className.startsWith("java.")
		}.className)
		if (!callingLocation.kotlin.isSubclassOf(EnergyBearingBlockEntity::class))
			throw IllegalStateException("ExpansibleEnergyHandler must be used in an EnergyBearingBlockEntity")
	}

	private val cells: MutableList<ExpansibleCell> = cells.toMutableList()

	fun getCell(cell: Int): ExpansibleCell = this.cells[cell]

	interface ExpansibleEnergyHolder {
		val energyStoredDecimal: BigDecimal
		val maxEnergyStoredDecimal: BigDecimal?
	}

	class ExpansibleCell(
		capacity: BigDecimal? = null,
		var maxIn: BigDecimal? = null,
		var maxOut: BigDecimal? = null,
		var amount: BigDecimal = BigDecimal.ZERO
	) : ExpansibleEnergyHolder {
		var capacity: BigDecimal? = capacity
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
		override val energyStoredDecimal: BigDecimal
			get() = this.amount
		override val maxEnergyStoredDecimal: BigDecimal?
			get() = this.capacity

		fun fill(count: BigDecimal, simulate: Boolean): BigDecimal {
			val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
			val saved = this.amount
			val sum = (saved + actualCount).let { this.capacity?.let { c -> it.min(c) } ?: it }
			if (!simulate) this.amount = sum
			return sum - saved
		}
	}

	override val energyStoredDecimal: BigDecimal
		get() = this.cells.sumOf { it.amount }
	override val maxEnergyStoredDecimal: BigDecimal?
		get() = this.cells.sumOf { it.capacity ?: return null }

	fun receiveEnergyDecimal(count: BigDecimal, simulate: Boolean): BigDecimal {
		var actualCount = count
		this.cells.forEachIndexed { cellIndex, cell ->
			val filled = cell.fill(this.receiveAction(actualCount, simulate, cellIndex) ?: actualCount, simulate)
			actualCount -= filled
		}
		return count - actualCount
	}

	override fun receiveEnergy(count: Int, simulate: Boolean): Int = this.receiveEnergyDecimal(
		count.toBigDecimal(),
		simulate
	).capInt()

	override fun extractEnergy(count: Int, simulate: Boolean): Int {
		TODO("Not yet implemented")
	}

	override fun getEnergyStored(): Int = this.energyStoredDecimal.capInt()
	override fun getMaxEnergyStored(): Int = this.maxEnergyStoredDecimal?.capInt() ?: Int.MAX_VALUE
	override fun canExtract(): Boolean = this.energyStored > 0
	override fun canReceive(): Boolean = this.maxEnergyStoredDecimal?.let { this.energyStoredDecimal < it } != false

	fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
		this.cells.forEachIndexed { index, cell ->
			tag.put("$index", CompoundTag().also { cellTag ->
				cellTag.putString("amount", cell.amount.toEngineeringString())
				cell.capacity?.let { cellTag.putString("capacity", it.toEngineeringString()) }
			})
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			val tank = this.cells[it.toInt()]
			val thisCompound = from.getCompound(it)
			tank.capacity =
				if (thisCompound.contains("capacity")) BigDecimal(thisCompound.getString("capacity"))
				else null
			tank.amount = BigDecimal(thisCompound.getString("amount"))
		}
	}
}