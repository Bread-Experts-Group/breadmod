package org.bread_experts_group.breadmod.experimental.particle

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.experimental.particle.Isotopes
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

object RadioactiveMaterial : Item(Properties()) {
	fun getClosedSystem(stack: ItemStack): ClosedSystem = stack.getOrDefault(
		ModDataComponents.CLOSED_SYSTEM,
		ClosedSystem(
			BigDecimal.ZERO,
			ConcurrentHashMap<Isotopes.Isotope, BigInteger>().also {
				it[Isotopes.URANIUM_234] = Isotopes.URANIUM_234.gramsToAtoms(5000)
			}
		)
	)

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (level.isClientSide) return
		val system = this.getClosedSystem(stack)
		for ((isotope, atoms) in system.substances) {
			if (isotope.halfLifeSeconds == null || atoms <= BigInteger.ZERO) continue
			val halfLives = BigDecimal(0.05).divide(isotope.halfLifeSeconds, Isotopes.mathContext)
			val remainder = atoms
				.toBigDecimal()
				.multiply(BigDecimal(0.5.pow(halfLives.toDouble()))) // TODO: PRECISION LOSS
				.toBigInteger()
			val decayed = atoms.subtract(remainder)
			for (statistic in isotope.decayStatistics) when (statistic) {
				is Isotopes.DecayStatisticDaughterAndEnergy -> {
					val newAmount = statistic.ratio.multiply(decayed.toBigDecimal())
					if (newAmount <= BigDecimal.ZERO) continue
					system.energy += newAmount.multiply(statistic.energyEV)
					val current = system.substances[statistic.daughter] ?: BigInteger.ZERO
					system.substances[statistic.daughter] = current + newAmount.toBigInteger()
				}
			}
			system.substances[isotope] = remainder
		}
		stack.set(ModDataComponents.CLOSED_SYSTEM, system)
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val system = this.getClosedSystem(stack)
		tooltipComponents.add(
			modTranslatable(
				"item", "radioactive_material",
				"energy", args = listOf(system.energy)
			)
		)
		tooltipComponents.add(Component.empty())
		system.substances.forEach { (isotope, atoms) ->
			tooltipComponents.add(Component.literal("${isotope.getTag()}: $atoms atoms"))
		}
		tooltipComponents.add(Component.empty())
		tooltipComponents.add(
			modTranslatable(
				"item", "radioactive_material",
				"total_energy", args = listOf(system.calculateEnergy())
			)
		)
	}
}