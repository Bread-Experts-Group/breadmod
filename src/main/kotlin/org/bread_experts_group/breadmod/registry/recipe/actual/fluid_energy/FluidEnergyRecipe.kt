package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.datafixers.util.Function6
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.BreadModRecipes
import kotlin.math.max

typealias FluidEnergyRecipeMulti = FluidEnergyRecipe<FluidEnergyInput>

typealias RecipeFunctionMulti =
			(
			NonNullList<SizedIngredient>, MutableList<ItemStack>,
			NonNullList<SizedFluidIngredient>, MutableList<FluidStack>,
			Int?, Int?
		) -> FluidEnergyRecipeMulti

typealias RecipeFunctionDataFixer<R> =
		Function6<NonNullList<SizedIngredient>, MutableList<ItemStack>,
				NonNullList<SizedFluidIngredient>, MutableList<FluidStack>,
				Int?, Int?, R>

abstract class FluidEnergyRecipe<T : FluidEnergyInput>(
	val rItemInputs: NonNullList<SizedIngredient>,
	val rItemOutputs: MutableList<ItemStack>,
	val rFluidInputs: NonNullList<SizedFluidIngredient>,
	val rFluidOutputs: MutableList<FluidStack>,
	rTime: Int?,
	rEnergy: Int?
) : BreadModRecipes<T>(rTime, rEnergy) {
	/**
	 * Compares [rItemInputs] and [rFluidInputs] with the [input]s items and fluids.
	 */
	override fun matches(input: T, level: Level): Boolean =
		this.rItemInputs.all { rItem ->
			input.iItems.any(rItem::test)
		} && this.rFluidInputs.all { rFluid ->
			input.iFluids.any(rFluid::test)
		} && super.matches(input, level)

	/**
	 * Assembles the first item in [rItemOutputs].
	 */
	override fun assemble(input: T, registries: HolderLookup.Provider): ItemStack =
		this.rItemOutputs[0].copyWithCount(this.rItemOutputs[0].count)

	/**
	 * @see assemble
	 */
	fun assembleItem(input: T, level: Level): ItemStack = this.assemble(input, level.registryAccess())

	/**
	 * Assembles the first fluid in [rFluidOutputs].
	 */
	fun assembleFluid(): FluidStack =
		this.rFluidOutputs[0].copyWithAmount(this.rFluidOutputs[0].amount)

	/**
	 * Gathers the output items in this recipe and returns a list of [ItemStack]s.
	 * @see consumeItems
	 * @see setItemsOverflow
	 */
	fun assembleItems(): List<ItemStack> =
		buildList {
			repeat(this@FluidEnergyRecipe.rItemOutputs.size) { index ->
				val count = this@FluidEnergyRecipe.rItemOutputs[index].count
				this.add(this@FluidEnergyRecipe.rItemOutputs[index].copyWithCount(count))
			}
		}

	/**
	 * Gathers the output fluids in this recipe and returns a list of [FluidStack]s.
	 * @see consumeFluids
	 * @see setFluidsOverflow
	 */
	fun assembleFluids(): List<FluidStack> =
		buildList {
			repeat(this@FluidEnergyRecipe.rFluidOutputs.size) { index ->
				val amount = this@FluidEnergyRecipe.rFluidOutputs[index].amount
				this.add(this@FluidEnergyRecipe.rFluidOutputs[index].copyWithAmount(amount))
			}
		}

	/**
	 * Combined [assembleItems] and [assembleFluids]
	 */
	fun assembleOutputs(): Pair<List<ItemStack>, List<FluidStack>> =
		this.assembleItems() to this.assembleFluids()

	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

	/**
	 * @return A copy of the first item in [rItemOutputs]
	 */
	override fun getResultItem(registries: HolderLookup.Provider): ItemStack = this.rItemOutputs[0].copy()

	/**
	 * Combined [consumeItems] and [consumeFluids].
	 */
	fun consumeInputs(items: List<ItemStack>, fluids: List<FluidStack>): Pair<List<ItemStack>, List<FluidStack>> =
		this.consumeItems(items) to this.consumeFluids(fluids)

	/**
	 * @param items The list of item slots to extract from.
	 * @return A list of items with the recipe input counts subtracted.
	 * @see consumeItemsAndSet
	 */
	fun consumeItems(items: List<ItemStack>): List<ItemStack> {
		val itemList: MutableList<ItemStack> = mutableListOf()
		this.rItemInputs.forEach { itemList.add(items.find(it::test) ?: return@forEach) }
		itemList.forEach { item -> this.rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
		return itemList
	}

	/**
	 * [consumeItems] with a [set] lambda provided for passing to the item handlers' set methods.
	 */
	fun consumeItemsAndSet(items: List<ItemStack>, set: (Int, ItemStack) -> Unit): Unit = items.forEachIndexed(set)

	/**
	 * @param fluids The list of tanks to extract from.
	 * @return A list of fluids with the recipe input amounts extracted.
	 * @see consumeFluidsAndSet
	 */
	fun consumeFluids(fluids: List<FluidStack>): List<FluidStack> {
		val fluidList: MutableList<FluidStack> = mutableListOf()
		this.rFluidInputs.forEach { fluidList.add(fluids.find(it::test) ?: return@forEach) }
		fluidList.forEach { fluid -> this.rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
		return fluidList
	}

	/**
	 * [consumeFluids] with a [set] lambda provided for passing to the fluid handlers' set methods.
	 */
	fun consumeFluidsAndSet(fluids: List<FluidStack>, set: (Int, FluidStack) -> Unit): Unit =
		fluids.forEachIndexed(set)

	/**
	 * @return True if both items and fluids are still valid.
	 * @see itemsStillValid
	 * @see fluidStillValid
	 */
	fun inputsStillValid(items: List<ItemStack>, fluids: List<FluidStack>): Boolean =
		this.itemsStillValid(items) && this.fluidsStillValid(fluids)

	/**
	 * @return True if the input items are still valid.
	 * @see itemStillValid
	 */
	fun itemsStillValid(items: List<ItemStack>): Boolean =
		this.rItemInputs.all { rItem -> items.any(rItem::test) }

	/**
	 * Single item variant of [itemsStillValid]
	 */
	fun itemStillValid(stack: ItemStack): Boolean = this.itemsStillValid(listOf(stack))

	/**
	 * @return True if the input fluids are still valid.
	 * @see fluidStillValid
	 */
	fun fluidsStillValid(fluids: List<FluidStack>): Boolean =
		this.rFluidInputs.all { rFluid -> fluids.any(rFluid::test) }

	/**
	 * Single fluid variant of [fluidStillValid]
	 */
	fun fluidStillValid(fluid: FluidStack): Boolean = this.fluidsStillValid(listOf(fluid))

	/**
	 * Gets the time of this current recipe.
	 */
	fun getTime(): Int = this.rTime ?: 0

	fun setEnergyDivision(): Int = (this.rEnergy ?: 0) / max(this.getTime(), 1)

	/**
	 * @return True if items fit in result slots and fluids fit in result tanks.
	 */
	fun canFitResults(items: List<ItemStack>, fluids: List<FluidStack>, tankCapacity: Int): Boolean =
		this.canFitItemResults(items) && this.canFitFluidResults(fluids, tankCapacity)
	// start overflow
	/**
	 * Overflow variant of [canFitItemResults]
	 *
	 * @param items The list of item slots to check for slot fitting.
	 */
	fun canFitItemsOverflow(items: List<ItemStack>): Boolean =
		this.rItemOutputs.all { outputItem ->
			items.indices.any { this.checkItemsCanFit(items[it], outputItem) }
		}

	/**
	 * Helper method for comparing a [source] ItemStack with the [target] ItemStack.
	 * Checks for item type matching and stack size.
	 */
	fun checkItemsCanFit(source: ItemStack, target: ItemStack): Boolean =
		(source.`is`(target.item) || source.isEmpty) && (source.count + target.count <= target.maxStackSize)

	/**
	 * Gathers the item results in [assembleItems] and sets the items in the output slots,
	 * overflowing into additional output slots if necessary.
	 *
	 * - [get] lambda is provided for getting the output slots for handling item setting.
	 * - [set] lambda is provided for setting the target slot with the item handlers' set methods
	 * @param range The slot range to gather output items from for determining where items will be set.
	 * @param slotOffset The starting offset for setting items in output slots.
	 */
	fun setItemsOverflow(
		range: IntRange,
		get: (IntRange) -> List<ItemStack>,
		set: (Int, ItemStack, Int) -> Unit,
		slotOffset: Int = 0
	) {
		for (outputItem in this.assembleItems()) {
			val outputItems = get(range)
			for (i in outputItems.indices)
				if (this.checkItemsCanFit(outputItems[i], outputItem)) {
					set(i + slotOffset, outputItem, outputItem.count)
					break
				}
		}
	}

	/**
	 * Overflow variant of [canFitFluidResults]
	 *
	 * @param fluids The list of fluid tanks to check for tank fitting.
	 */
	fun canFitFluidsOverflow(fluids: List<FluidStack>, tankCapacity: Int): Boolean =
		this.rFluidOutputs.all { outputFluid ->
			fluids.indices.any { this.checkFluidsCanFit(fluids[it], outputFluid, tankCapacity) }
		}

	/**
	 * Helper method for comparing a [source] FluidStack with the [target] FluidStack.
	 * Checks for fluid type matching and tank capacity.
	 */
	fun checkFluidsCanFit(source: FluidStack, target: FluidStack, tankCapacity: Int): Boolean =
		(source.`is`(target.fluid) || source.isEmpty) && (source.amount + target.amount <= tankCapacity)

	/**
	 * Gathers the fluid results in [assembleFluids] and sets the fluid in the output tanks,
	 * overflowing into additional output tanks if necessary.
	 *
	 * - [get] lambda is provided for getting the output tanks for handling fluid setting.
	 * - [set] lambda is provided for setting the target tank with the fluid handlers' set methods
	 * @param range The tank range to gather output fluids from for determining where fluids will be set.
	 * @param tankOffset The starting offset for setting fluid in output tanks.
	 */
	fun setFluidsOverflow(
		range: IntRange,
		get: (IntRange) -> List<FluidStack>,
		set: (Int, FluidStack, Int) -> Unit,
		tankCapacity: Int,
		tankOffset: Int = 0
	) {
		for (outputFluid in this.assembleFluids()) {
			val outputFluids = get(range)
			for (i in outputFluids.indices)
				if (this.checkFluidsCanFit(outputFluids[i], outputFluid, tankCapacity)) {
					set(i + tankOffset, outputFluid, outputFluid.amount)
					break
				}
		}
	}
	// end overflow
	/**
	 * @return True if items fit in result slots, false otherwise.
	 */
	fun canFitItemResults(items: List<ItemStack>): Boolean =
		items.all { iItem ->
			this.rItemOutputs.any { rItem ->
				iItem.count < rItem.maxStackSize || iItem.count + rItem.count < rItem.maxStackSize
			}
		} || this.rItemOutputs.isEmpty()

	/**
	 * Single item variant of [canFitItemResults]
	 */
	fun canFitItemResult(stack: ItemStack): Boolean = this.canFitItemResults(listOf(stack))

	/**
	 * @return True if fluids fit in result tanks, false otherwise.
	 */
	fun canFitFluidResults(fluids: List<FluidStack>, tankCapacity: Int): Boolean =
		fluids.all { iFluid ->
			this.rFluidOutputs.any { rFluid ->
				iFluid.amount <= tankCapacity || iFluid.amount + rFluid.amount <= tankCapacity
			}
		} || this.rFluidOutputs.isEmpty()

	/**
	 * Single fluid variant of [canFitFluidResults]
	 */
	fun canFitFluidResult(fluid: FluidStack, tankCapacity: Int): Boolean =
		this.canFitFluidResults(listOf(fluid), tankCapacity)

	abstract override fun getSerializer(): RecipeSerializer<*>
	abstract override fun getType(): RecipeType<*>
}