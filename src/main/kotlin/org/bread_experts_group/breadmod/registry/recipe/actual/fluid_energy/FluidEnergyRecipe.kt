package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.datafixers.util.Function6
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.util.int
import java.math.BigDecimal
import java.util.Optional

/** Convenience type for [FluidEnergyBuilder]. */
typealias RecipeFunctionMulti =
			(
			List<BigDescriptor<Item>>, List<BigDescriptor<Item>>,
			List<BigDescriptor<Fluid>>, List<BigDescriptor<Fluid>>,
			ULong, BigDecimal?
		) -> FluidEnergyRecipe

/** Convenience type for [FluidEnergySerializer] and [ModRecipeSerializers]. */
typealias RecipeFunctionDataFixer<R> =
		Function6<Optional<List<BigDescriptor<Item>>>, Optional<List<BigDescriptor<Item>>>,
				Optional<List<BigDescriptor<Fluid>>>, Optional<List<BigDescriptor<Fluid>>>,
				ULong, Optional<BigDecimal>, R>

data class BigDescriptor<T>(
	val amount: BigDecimal,
	val value: T,
	val components: DataComponentMap = DataComponentMap.EMPTY
) {
	constructor(
		amount: Int, value: T,
		components: DataComponentMap = DataComponentMap.EMPTY
	) : this(BigDecimal(amount), value, components)

	operator fun minus(other: BigDecimal): BigDescriptor<T> {
		return BigDescriptor(
			this.amount - other,
			this.value,
			this.components
		)
	}
}

fun BigDescriptor<Item>.itemStack(): ItemStack {
	val stack = ItemStack(this.value, this.amount.int)
	stack.applyComponents(this.components)
	return stack
}

/**
 * An "All in one" recipe class for all of BreadMod's recipes.
 *
 * - Handles fluids, items, time, and energy.
 */
abstract class FluidEnergyRecipe(
	/** Input list of items for this recipe. Populated via [FluidEnergyInput]. */
	val rItemInputs: List<BigDescriptor<Item>>,
	/** Output list of items for this recipe. Populated via [RecipeManager]. */
	val rItemOutputs: List<BigDescriptor<Item>>,
	/** Input list of fluids for this recipe. Populated via [FluidEnergyInput]. */
	val rFluidInputs: List<BigDescriptor<Fluid>>,
	/** Output list of fluids for this recipe. Populated via [RecipeManager]. */
	val rFluidOutputs: List<BigDescriptor<Fluid>>,
	/** The amount of time needed for this recipe to complete.*/
	val rTime: ULong,
	/** The amount of energy needed for this recipe to complete.*/
	val rEnergy: BigDecimal?
) : Recipe<FluidEnergyInput> {
	/**
	 * Compares [rItemInputs] and [rFluidInputs] with the [input]s items and fluids.
	 */
	override fun matches(input: FluidEnergyInput, level: Level): Boolean {
		val reliesOnItems = this.rItemInputs.isNotEmpty()
		if (reliesOnItems && input.item == null) return false
		val reliesOnFluids = this.rFluidInputs.isNotEmpty()
		if (reliesOnFluids && input.fluid == null) return false
		val reliesOnEnergy = this.rEnergy != null && this.rEnergy != BigDecimal.ZERO
		if (reliesOnEnergy && input.energy == null) return false
		// TODO: This all ignores components, we need to differentiate them
		if (reliesOnItems) {
			val count = mutableMapOf<Item, BigDecimal>()
			input.item!!.slots.forEach { (_, slot) ->
				count.compute(slot.item) { _, acc -> slot.amount + (acc ?: BigDecimal.ZERO) }
			}
			this.rItemInputs.forEach {
				val accumulated = count[it.value] ?: return false
				if (accumulated < it.amount) return false
			}
		}
		if (reliesOnFluids) throw UnsupportedOperationException()
		if (reliesOnEnergy) throw UnsupportedOperationException()
		return true
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 *
	 * ##### Javadoc copied from superclass.
	 */
	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

	fun consumeItemsAndFluids(input: FluidEnergyInput) {
		this.rItemInputs.forEach {
			var remainder = it.amount
			for (slotID in input.item!!.slots.keys) {
				val extracted = input.item.bigExtractItem(slotID, remainder, true)
				if (extracted.value == it.value) {
					input.item.bigExtractItem(slotID, remainder, false)
					remainder -= extracted.amount
					if (remainder <= BigDecimal.ZERO) break
				}
			}
		}
		// TODO Fluids
	}

	val consumedPerProgress: BigDecimal? = this.rEnergy?.divide(BigDecimal(this.rTime.toString()))
	fun consumeEnergyTick(input: FluidEnergyInput): Boolean {
		if (this.consumedPerProgress != null) {
			val extracted = input.energy!!.extractBigEnergy(
				this.consumedPerProgress - input.lastConsumed,
				false
			)
			if ((extracted + input.lastConsumed) < this.consumedPerProgress) {
				input.lastConsumed += extracted
				return false
			}
			input.lastConsumed = BigDecimal.ZERO
			return true
		}
		return false
	}

	override fun assemble(input: FluidEnergyInput, registries: HolderLookup.Provider): ItemStack {
		this.rItemOutputs.forEach {
			var remainder = it.amount
			for (slotID in input.item!!.slots.keys) {
				val inserted = input.item.bigInsertItem(
					slotID,
					BigDescriptor(remainder, it.value, it.components),
					false
				)
				remainder = inserted.amount
				if (remainder <= BigDecimal.ZERO) break
			}
		}
		// TODO Fluids
		return ItemStack.EMPTY
	}

	override fun getResultItem(registries: HolderLookup.Provider): ItemStack = throw UnsupportedOperationException()
}