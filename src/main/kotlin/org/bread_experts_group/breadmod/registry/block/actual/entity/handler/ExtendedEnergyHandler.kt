package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.Color.LIGHT_GRAY
import org.bread_experts_group.breadmod.util.Color.component
import org.bread_experts_group.breadmod.util.floatRoundEven
import org.bread_experts_group.breadmod.util.int
import java.math.BigDecimal
import kotlin.math.roundToInt

class ExtendedEnergyHandler(
	bigCapacity: BigDecimal,
	private val maxIn: BigDecimal = bigCapacity,
	private val maxOut: BigDecimal = bigCapacity
) : ParentedHandler<BreadModBlockEntity>, IEnergyStorage, DataComponentSerializable, INBTSerializable<Tag> {
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	override lateinit var parent: BreadModBlockEntity
	var bigAmount: BigDecimal = BigDecimal.ZERO
		private set
	var bigCapacity: BigDecimal = bigCapacity
		private set(value) {
			this.bigAmount = minOf(this.bigAmount, value)
			field = value
		}

	override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag = CompoundTag().also {
		it.putString("amount", this.bigAmount.toString())
		it.putString("capacity", this.bigCapacity.toString())
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: Tag) {
		if (nbt !is CompoundTag) return
		this.bigAmount = try {
			BigDecimal(nbt.getString("amount"))
		} catch (_: NumberFormatException) {
			BigDecimal.ZERO
		}
		this.bigCapacity = try {
			BigDecimal(nbt.getString("capacity"))
		} catch (_: NumberFormatException) {
			BigDecimal.ONE
		}
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		this.bigAmount = from.getOrDefault(ModDataComponents.ENERGY, this.bigAmount)
		this.bigCapacity = from.getOrDefault(ModDataComponents.ENERGY_CAPACITY, this.bigCapacity)
	}

	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		map.set(ModDataComponents.ENERGY, this.bigAmount)
		map.set(ModDataComponents.ENERGY_CAPACITY, this.bigCapacity)
	}

	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		val energy = JadeDrawingCommon.fixedLengthScrollingComponent(
			this.bigAmount,
			this.bigCapacity,
			"FE",
			Color.RED
		)
		tooltipComponents.add(energy)
		val maxOut = "↑ ".component(LIGHT_GRAY)
		maxOut.append(JadeDrawingCommon.fixedLengthScrollingComponentSinglet(this.maxOut, "FE", Color.RED))
		tooltipComponents.add(maxOut)
		val maxIn = "↓ ".component(LIGHT_GRAY)
		maxIn.append(JadeDrawingCommon.fixedLengthScrollingComponentSinglet(this.maxIn, "FE", Color.RED))
		tooltipComponents.add(maxIn)
	}

	fun receiveBigEnergy(toReceive: BigDecimal, simulate: Boolean): BigDecimal {
		val transfer = minOf(toReceive, this.bigCapacity - this.bigAmount, this.maxIn)
		if (transfer < BigDecimal.ONE) return BigDecimal.ZERO
		if (!simulate) {
			this.bigAmount += transfer
			this.stateUpdated()
		}
		return transfer
	}

	override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
		return this.receiveBigEnergy(BigDecimal(toReceive), simulate).intValueExact()
	}

	fun extractBigEnergy(toExtract: BigDecimal, simulate: Boolean): BigDecimal {
		val transfer = minOf(toExtract, this.bigAmount, this.maxOut)
		if (!simulate) {
			this.bigAmount -= transfer
			this.stateUpdated()
		}
		return transfer
	}

	override fun extractEnergy(toExtract: Int, simulate: Boolean): Int = this.extractBigEnergy(
		BigDecimal(toExtract), simulate
	).int

	override fun canReceive(): Boolean = this.maxIn > BigDecimal.ZERO
	override fun canExtract(): Boolean = this.maxOut > BigDecimal.ZERO
	override fun getMaxEnergyStored(): Int = Int.MAX_VALUE
	override fun getEnergyStored(): Int = (this.bigAmount.divide(this.bigCapacity, floatRoundEven)
		.toFloat() * Int.MAX_VALUE).roundToInt()
}