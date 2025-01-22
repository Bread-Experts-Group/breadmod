package org.bread_experts_group.breadmod.util.handlers

import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.util.capInt
import java.math.BigDecimal
import kotlin.reflect.full.isSubclassOf

open class ExpansibleEnergyHandler(
	override val units: MutableList<ExpansibleCell>
) : AbstractExpansibleHandler<ExpansibleEnergyHandler.ExpansibleCell>(), IEnergyStorage {
	init {
		val stackTrace = Thread.currentThread().stackTrace
		val callingLocation = this::class.java.classLoader.loadClass(stackTrace.first {
			it.className != this::class.qualifiedName && !it.className.startsWith("java.")
		}.className)
		if (!callingLocation.kotlin.isSubclassOf(EnergyBearingBlockEntity::class))
			throw IllegalStateException("ExpansibleEnergyHandler must be used in an EnergyBearingBlockEntity")
	}

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
	).first.capInt()

	override fun extractEnergy(count: Int, simulate: Boolean): Int = this.drainDecimal(
		count.toBigDecimal(),
		simulate
	).first.capInt()

	override fun getEnergyStored(): Int = this.amount.capInt()
	override fun getMaxEnergyStored(): Int = this.capacity?.capInt() ?: Int.MAX_VALUE
	override fun canExtract(): Boolean = this.energyStored > 0
	override fun canReceive(): Boolean = this.capacity?.let { this.amount < it } != false
}