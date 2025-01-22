package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.util.capInt
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler.ExpansibleEnergyHolder
import org.bread_experts_group.breadmod.util.handlers.HandlerCommon.calculateAndSave
import java.math.BigDecimal
import kotlin.reflect.full.isSubclassOf

open class ExpansibleEnergyHandler(
	cells: List<ExpansibleCell>,
	override var receiveAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
	override var extractAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? =
		{ _, _, _ -> null },
) : IEnergyStorage, ExpansibleEnergyHolder, HandlerListener {
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
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : ExpansibleEnergyHolder, HandlerLimits {
		override var capacity: BigDecimal? = capacity
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
		override val energyStoredDecimal: BigDecimal
			get() = this.amount
		override val maxEnergyStoredDecimal: BigDecimal?
			get() = this.capacity

		fun fillDecimal(count: BigDecimal, simulate: Boolean): BigDecimal = this.calculateAndSave(count, simulate)
	}

	override val energyStoredDecimal: BigDecimal
		get() = this.cells.sumOf { it.amount }
	override val maxEnergyStoredDecimal: BigDecimal?
		get() = this.cells.sumOf { it.capacity ?: return null }

	fun receiveEnergyDecimal(count: BigDecimal, simulate: Boolean): BigDecimal {
		var actualCount = count
		for (cellIndex in this.cells.indices) {
			val cell = this.cells[cellIndex]
			if (cell.maxIn == BigDecimal.ZERO) continue
			val filled = cell.fillDecimal(
				this.receiveAction(actualCount, simulate, cellIndex) ?: actualCount,
				simulate
			)
			actualCount -= filled
			if (actualCount == BigDecimal.ZERO) break
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