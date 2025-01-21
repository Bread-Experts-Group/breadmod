package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.util.capInt
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler.ExpansibleEnergyHolder
import java.math.BigDecimal

open class ExpansibleEnergyHandler(
	cells: List<ExpansibleCell>,
	var receiveAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? = { _, _, _ -> null },
	var extractAction: (count: BigDecimal, simulate: Boolean, cellIndex: Int) -> BigDecimal? = { _, _, _ -> null },
) : IEnergyStorage, ExpansibleEnergyHolder {
	private val cells: MutableList<ExpansibleCell> = cells.toMutableList()

	fun getCell(cell: Int): ExpansibleCell = this.cells[cell]

	interface ExpansibleEnergyHolder {
		val energyStoredDecimal: BigDecimal
		val maxEnergyStoredDecimal: BigDecimal?
	}

	class ExpansibleCell(
		var capacity: BigDecimal? = null,
		var maxIn: BigDecimal? = null,
		var maxOut: BigDecimal? = null,
		var amount: BigDecimal = BigDecimal.ZERO
	) : ExpansibleEnergyHolder {
		override val energyStoredDecimal: BigDecimal
			get() = this.amount
		override val maxEnergyStoredDecimal: BigDecimal?
			get() = this.capacity

		fun fill(count: BigDecimal, simulate: Boolean): BigDecimal {
			val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
			val saved = this.amount
			val sum = (saved + actualCount).min(this.capacity)
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
		var sum = BigDecimal.ZERO
		this.cells.forEachIndexed { cellIndex, cell ->
			val filled = cell.fill(this.receiveAction(actualCount, simulate, cellIndex) ?: actualCount, simulate)
			actualCount -= filled
			filled
		}
		return sum
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
	override fun canReceive(): Boolean = this.energyStoredDecimal < this.maxEnergyStoredDecimal

	fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
		this.cells.forEachIndexed { index, cell ->
			tag.put("$index", CompoundTag().also { tankTag ->
				tankTag.putString("amount", cell.amount.toEngineeringString())
				tankTag.putString("capacity", cell.amount.toEngineeringString())
			})
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			val tank = this.cells[it.toInt()]
			tank.capacity = BigDecimal(from.getCompound(it).getString("capacity"))
			tank.amount = BigDecimal(from.getCompound(it).getString("amount"))
		}
	}
}