package org.bread_experts_group.breadmod.experimental.particle

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.common.util.INBTSerializable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap

// Ψ Ψ Ψ
data class ClosedSystem(
	var energy: BigDecimal,
	val substances: ConcurrentHashMap<Isotopes.Isotope, BigInteger>
) : INBTSerializable<CompoundTag> {
	fun totalMass(): BigInteger = this.substances.values
		.fold(BigInteger.ZERO, BigInteger::add)

	fun calculateEnergyMass(): BigInteger = this.totalMass()
		.multiply(Companion.c2)
		.divide(Companion.thousand)

	fun calculateEnergy(): BigDecimal = this.energy
		.multiply(Companion.eVToJoule)
		.add(this.calculateEnergyMass().toBigDecimal())

	fun toNBT(): CompoundTag = CompoundTag().also {
		it.putString("energy", this.energy.toString())
		val substancesTag = CompoundTag()
		this.substances.forEach { (isotope, atoms) ->
			substancesTag.putString(isotope.getTag(), atoms.toString())
		}
		it.put("substances", substancesTag)
	}

	fun fromNBT(tag: CompoundTag) {
		this.energy = BigDecimal(tag.getString("energy"))
		val substancesTag = tag.getCompound("substances")
		substancesTag.allKeys.forEach {
			val isotope = Isotopes.isotopes.getValue(it)
			val value = substancesTag.getString(it)
			this.substances[isotope] = BigInteger(value)
		}
	}

	override fun serializeNBT(p0: HolderLookup.Provider): CompoundTag = this.toNBT()
	override fun deserializeNBT(p0: HolderLookup.Provider, tag: CompoundTag): Unit = this.fromNBT(tag)

	companion object {
		val thousand: BigInteger = BigInteger.valueOf(1000)
		val c2: BigInteger = BigInteger.valueOf(299792458).pow(2)
		val eVToJoule: BigDecimal = BigDecimal("1.602176634e-19")

		fun createFromTag(compoundTag: CompoundTag): ClosedSystem {
			val newSystem = ClosedSystem(BigDecimal.ZERO, ConcurrentHashMap())
			newSystem.fromNBT(compoundTag)
			return newSystem
		}
	}
}