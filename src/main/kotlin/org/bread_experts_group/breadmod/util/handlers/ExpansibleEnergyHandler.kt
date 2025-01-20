package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.energy.IEnergyStorage
import java.math.BigDecimal

open class ExpansibleEnergyHandler(
	private val cellCapacities: MutableList<BigDecimal?>,
	var maxIn: BigDecimal? = null,
	var maxOut: BigDecimal? = null,
	var receiveAction: (count: Int, simulate: Boolean) -> BigDecimal? = { _, _ -> null },
	var extractAction: (count: Int, simulate: Boolean) -> BigDecimal? = { _, _ -> null },
) : IEnergyStorage {
	private val cells: MutableList<BigDecimal> = MutableList(this.cellCapacities.size) { BigDecimal.ZERO }

	override fun receiveEnergy(count: Int, simulate: Boolean): Int {
		TODO("Not yet implemented")
	}

	override fun extractEnergy(count: Int, simulate: Boolean): Int {
		TODO("Not yet implemented")
	}

	val energyStoredDecimal: BigDecimal
		get() = this.cells.sumOf { it }
	val maxEnergyStoredDecimal: BigDecimal
		get() {
			var sum = BigDecimal.ZERO
			for (capacity in this.cellCapacities) {
				if (capacity == null) return this.energyStoredDecimal + BigDecimal.ONE
				sum += capacity
			}
			return sum
		}

	override fun getEnergyStored(): Int =
		if (this.energyStoredDecimal > Int.MAX_VALUE.toBigDecimal()) Int.MAX_VALUE
		else this.energyStoredDecimal.toInt()

	override fun getMaxEnergyStored(): Int =
		if (this.maxEnergyStoredDecimal > Int.MAX_VALUE.toBigDecimal()) Int.MAX_VALUE
		else this.maxEnergyStoredDecimal.toInt()

	override fun canExtract(): Boolean =
		this.energyStored > 0 && this.maxOut.let { it == null || (it > BigDecimal.ZERO) }

	override fun canReceive(): Boolean =
		this.energyStoredDecimal < this.maxEnergyStoredDecimal &&
				this.maxIn.let { it == null || (it > BigDecimal.ZERO) }

	fun serializeNBT(): CompoundTag = CompoundTag().also { tag ->
		this.cells.forEachIndexed { index, cell ->
			tag.putString("$index", cell.toEngineeringString())
		}
	}

	fun deserializeNBT(from: CompoundTag) {
		from.allKeys.forEach {
			this.cells[it.toInt()] = BigDecimal(from.getString(it))
		}
	}
}