package org.bread_experts_group.breadmod.util.handlers

import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.util.capInt
import java.math.BigDecimal

class ExpansibleEnergyHandler(
	override val units: MutableList<ExpansibleCell>
) : AbstractExpansibleHandler<ExpansibleEnergyHandler.ExpansibleCell>(), IEnergyStorage {
	class ExpansibleCell(
		capacity: BigDecimal? = null,
		override var maxIn: BigDecimal? = null,
		override var maxOut: BigDecimal? = null,
		override var amount: BigDecimal = BigDecimal.ZERO
	) : HandlerSerializable {
		constructor(
			capacity: Int,
			allowIn: Boolean,
			allowOut: Boolean,
			amount: BigDecimal = BigDecimal.ZERO
		) : this(
			capacity.toBigDecimal(),
			if (allowIn) capacity.toBigDecimal() else BigDecimal.ZERO,
			if (allowOut) capacity.toBigDecimal() else BigDecimal.ZERO,
			amount
		)

		override var capacity: BigDecimal? = capacity
			set(value) {
				field = if (value != null && value <= BigDecimal.ZERO) null else value
			}
	}

	override fun receiveEnergy(count: Int, simulate: Boolean): Int = this.fillDecimal(
		count.toBigDecimal(),
		simulate
	).first.capInt().also { if (it != 0) this.changed() }

	override fun extractEnergy(count: Int, simulate: Boolean): Int = this.drainDecimal(
		count.toBigDecimal(),
		simulate
	).first.capInt().also { if (it != 0) this.changed() }

	override fun getEnergyStored(): Int = this.amount.capInt()
	override fun getMaxEnergyStored(): Int = this.capacity?.capInt() ?: Int.MAX_VALUE
	override fun canExtract(): Boolean = this.energyStored > 0
	override fun canReceive(): Boolean = this.capacity?.let { this.amount < it } != false
}