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
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BreadModRecipes
import kotlin.math.max

typealias FluidEnergyRecipeMulti = FluidEnergyRecipe<FluidEnergyInput>

typealias RecipeFunctionMulti = (
	NonNullList<SizedIngredient>,
	MutableList<ItemStack>,
	NonNullList<SizedFluidIngredient>,
	MutableList<FluidStack>,
	Int?,
	Int?
) -> FluidEnergyRecipeMulti

typealias RecipeFunctionDataFixer<R> = Function6<
		NonNullList<SizedIngredient>,
		MutableList<ItemStack>,
		NonNullList<SizedFluidIngredient>,
		MutableList<FluidStack>,
		Int?,
		Int?,
		R
		>

abstract class FluidEnergyRecipe<T : FluidEnergyInput>(
	val rItemInputs: NonNullList<SizedIngredient>,
	val rItemOutputs: MutableList<ItemStack>,
	val rFluidInputs: NonNullList<SizedFluidIngredient>,
	val rFluidOutputs: MutableList<FluidStack>,
	rTime: Int?,
	rEnergy: Int?
) : BreadModRecipes<T>(rTime, rEnergy) {
	override fun matches(input: T, level: Level): Boolean =
		this.rItemInputs.all { rItem ->
			input.iItems.any(rItem::test)
		} && this.rFluidInputs.all { rFluid ->
			input.iFluids.any(rFluid::test)
		} && super.matches(input, level)

	override fun assemble(input: T, registries: HolderLookup.Provider): ItemStack =
		this.rItemOutputs[0].copyWithCount(this.rItemOutputs[0].count)

	fun assemble(input: T, level: Level): ItemStack = this.assemble(input, level.registryAccess())

	fun assembleFluid(): FluidStack =
		this.rFluidOutputs[0].copyWithAmount(this.rFluidOutputs[0].amount)

	fun assembleItems(): List<ItemStack> =
		buildList {
			repeat(this@FluidEnergyRecipe.rItemOutputs.size) { index ->
				val count = this@FluidEnergyRecipe.rItemOutputs[index].count
				this.add(this@FluidEnergyRecipe.rItemOutputs[index].copyWithCount(count))
			}
		}

	fun assembleFluids(): List<FluidStack> =
		buildList {
			repeat(this@FluidEnergyRecipe.rFluidOutputs.size) { index ->
				val amount = this@FluidEnergyRecipe.rFluidOutputs[index].amount
				this.add(this@FluidEnergyRecipe.rFluidOutputs[index].copyWithAmount(amount))
			}
		}

	fun assembleOutputs(): Pair<List<ItemStack>, List<FluidStack>> =
		this.assembleItems() to this.assembleFluids()

	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true
	override fun getResultItem(registries: HolderLookup.Provider): ItemStack = this.rItemOutputs[0].copy()

	fun consumeInputs(items: List<ItemStack>, fluids: List<FluidStack>): Pair<List<ItemStack>, List<FluidStack>> =
		this.consumeItems(items) to this.consumeFluids(fluids)

	fun consumeItems(items: List<ItemStack>): List<ItemStack> {
		val itemList: MutableList<ItemStack> = mutableListOf()
		this.rItemInputs.forEach { itemList.add(items.find(it::test) ?: return@forEach) }
		itemList.forEach { item -> this.rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
		return itemList
	}

	fun consumeItemsAndSet(items: List<ItemStack>, set: (Int, ItemStack) -> Unit): Unit = items.forEachIndexed(set)

	fun consumeFluids(fluids: List<FluidStack>): List<FluidStack> {
		val fluidList: MutableList<FluidStack> = mutableListOf()
		this.rFluidInputs.forEach { fluidList.add(fluids.find(it::test) ?: return@forEach) }
		fluidList.forEach { fluid -> this.rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
		return fluidList
	}

	/**
	 * @return True if both items and fluids are still valid.
	 */
	fun inputsStillValid(items: List<ItemStack>, fluids: List<FluidStack>): Boolean =
		this.itemsStillValid(items) && this.fluidsStillValid(fluids)

	/**
	 * @return True if the input items are still valid.
	 */
	fun itemsStillValid(items: List<ItemStack>): Boolean =
		this.rItemInputs.all { rItem -> items.any(rItem::test) }

	fun itemStillValid(stack: ItemStack): Boolean =
		this.rItemInputs.all { rItem -> rItem.test(stack) }

	/**
	 * @return True if the input fluids are still valid.
	 */
	fun fluidsStillValid(fluids: List<FluidStack>): Boolean =
		this.rFluidInputs.all { rFluid -> fluids.any(rFluid::test) }

	fun getTime(): Int = this.rTime ?: 0

	fun setEnergyDivision(): Int = (this.rEnergy ?: 0) / max(this.getTime(), 1)

	/**
	 * @return True if items fit in result slots and fluids fit in result tanks.
	 */
	fun canFitResults(items: List<ItemStack>, fluids: List<FluidStack>, tankCapacity: Int): Boolean =
		this.canFitItemResults(items) && this.canFitFluidResults(fluids, tankCapacity)

	// start overflow
	fun canFitItemsOverflow(items: List<ItemStack>): Boolean =
		this.rItemOutputs.all { outputItem ->
			items.indices.any { this.checkItemsCanFit(items[it], outputItem) }
		}

	fun checkItemsCanFit(source: ItemStack, target: ItemStack): Boolean =
		(source.`is`(target.item) || source.isEmpty) && (source.count + target.count <= target.maxStackSize)

	fun setItemsOverflow(outputItems: List<ItemStack>, set: (Int, ItemStack, Int) -> Unit) {
		for (outputItem in this.assembleItems())
			for (i in outputItems.indices)
				if (this.checkItemsCanFit(outputItems[i], outputItem)) {
					set(i, outputItem, outputItem.count)
					break
				}
	}

	fun canFitFluidsOverflow(fluids: List<FluidStack>, tankCapacity: Int): Boolean =
		this.rFluidOutputs.all { outputFluid ->
			fluids.indices.any { this.checkFluidsCanFit(fluids[it], outputFluid, tankCapacity) }
		}

	fun checkFluidsCanFit(source: FluidStack, target: FluidStack, tankCapacity: Int): Boolean =
		(source.`is`(target.fluid) || source.isEmpty) && (source.amount + target.amount <= tankCapacity)

	fun setFluidsOverflow(
		outputFluids: List<FluidStack>,
		tankCapacity: Int,
		set: (Int, FluidStack, Int) -> Unit
	) {
		for (outputFluid in this.assembleFluids())
			for (i in outputFluids.indices)
				if (this.checkFluidsCanFit(outputFluids[i], outputFluid, tankCapacity)) {
					set(i, outputFluid, outputFluid.amount)
					break
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

	abstract override fun getSerializer(): RecipeSerializer<*>
	abstract override fun getType(): RecipeType<*>
}