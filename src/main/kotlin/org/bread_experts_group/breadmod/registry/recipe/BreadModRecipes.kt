package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler.ExpansibleTank

abstract class BreadModRecipes<T : RecipeInput>(val rTime: Int?, val rEnergy: Int?) : Recipe<T> {
	override fun matches(input: T, level: Level): Boolean = this.rTime!! >= 0 && this.rEnergy!! >= 0

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	override fun canCraftInDimensions(width: Int, height: Int): Boolean = true
	abstract class SingleFluid(
		val rFluidInput: SizedFluidIngredient,
		val rFluidOutput: FluidStack,
		rTime: Int?,
		rEnergy: Int?
	) : BreadModRecipes<org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs.SingleFluid>(rTime, rEnergy) {
		override fun matches(input: BMRecipeInputs.SingleFluid, level: Level): Boolean =
			this.rFluidInput.test(input.iFluid) && super.matches(input, level)

		override fun assemble(input: BMRecipeInputs.SingleFluid, registries: Provider): ItemStack =
			ItemStack.EMPTY

		fun assembleFluid(): FluidStack =
			this.rFluidOutput.copyWithAmount(this.rFluidOutput.amount)

		override fun getResultItem(registries: Provider): ItemStack = ItemStack.EMPTY
		override fun canCraftInDimensions(width: Int, height: Int): Boolean = true
		fun inputStillValid(fluid: FluidStack): Boolean = this.rFluidInput.test(fluid)
		fun canFitResults(tank: ExpansibleTank): Boolean =
			tank.amount < tank.capacity || tank.amount + this.rFluidOutput.amount.toBigDecimal() < tank.capacity

		fun consumeInput(tank: ExpansibleTank): FluidStack =
			tank.drain(this.rFluidInput.amount(), EXECUTE)
	}

	abstract class SingleItem(
		val rItemInput: SizedIngredient,
		val rItemOutput: ItemStack,
		rTime: Int?,
		rEnergy: Int?
	) : BreadModRecipes<org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs.SingleItem>(rTime, rEnergy) {
		override fun matches(input: BMRecipeInputs.SingleItem, level: Level): Boolean =
			this.rItemInput.test(input.iItem) && super.matches(input, level)

		override fun assemble(input: BMRecipeInputs.SingleItem, provider: Provider): ItemStack =
			this.rItemOutput.copyWithCount(this.rItemOutput.count)

		override fun getResultItem(registries: Provider): ItemStack = this.rItemOutput.copy()
		override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1

		/**
		 * Consumes the input item in slot 0.
		 * ### Expected slot should always be index 0.
		 */
		fun consumeInput(items: List<ItemStack>): Unit = items[0].shrink(this.rItemInput.count())
		fun inputStillValid(items: List<ItemStack>): Boolean = this.rItemInput.test(items[0])
		fun canFitResults(items: List<ItemStack>, itemIndex: Int): Boolean =
			(items[itemIndex].count < items[itemIndex].maxStackSize ||
					items[itemIndex].count + this.rItemOutput.count < items[itemIndex].maxStackSize)
	}

	abstract class MultiFluid(
		val rFluidInputs: NonNullList<SizedFluidIngredient>,
		val rFluidOutputs: List<FluidStack>,
		rTime: Int?,
		rEnergy: Int?
	) : BreadModRecipes<org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs.MultiFluid>(rTime, rEnergy) {
		override fun matches(input: BMRecipeInputs.MultiFluid, level: Level): Boolean =
			this.rFluidInputs.all { rFluid ->
				input.iFluids.any(rFluid::test)
			} && super.matches(input, level)

		override fun assemble(input: BMRecipeInputs.MultiFluid, registries: Provider): ItemStack =
			ItemStack.EMPTY

		fun assembleFluids(): List<FluidStack> = buildList {
			repeat(this@MultiFluid.rFluidOutputs.size) { index ->
				this.add(
					this@MultiFluid.rFluidOutputs[index].copyWithAmount(this@MultiFluid.rFluidOutputs[index].amount)
				)
			}
		}

		override fun getResultItem(registries: Provider): ItemStack = ItemStack.EMPTY
		override fun canCraftInDimensions(width: Int, height: Int): Boolean = true
		fun consumeInputs(fluids: List<FluidStack>) {
			val list: MutableList<FluidStack> = mutableListOf()
			this.rFluidInputs.forEach { list.add(fluids.find(it::test) ?: return@forEach) }
			list.forEach { fluid -> this.rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
		}

		fun inputsStillValid(fluids: List<FluidStack>): Boolean =
			this.rFluidInputs.all { rFluid -> fluids.any(rFluid::test) }

		fun canFitResults(fluids: List<FluidStack>, capacity: Int): Boolean =
			fluids.all { iFluid ->
				this.rFluidOutputs.any { rFluid ->
					iFluid.amount < capacity || iFluid.amount + rFluid.amount < capacity
				}
			}
	}

	abstract class MultiItem(
		val rItemInputs: NonNullList<SizedIngredient>,
		val rItemOutputs: List<ItemStack>,
		rTime: Int?,
		rEnergy: Int?
	) : BreadModRecipes<org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs.MultiItem>(rTime, rEnergy) {
		// todo account for split stacks of matching items
		override fun matches(input: BMRecipeInputs.MultiItem, level: Level): Boolean =
			this.rItemInputs.all { rItem ->
				input.iItems.any(rItem::test)
			} && super.matches(input, level)

		/**
		 * @return The first item in [rItemOutputs]
		 */
		override fun assemble(input: BMRecipeInputs.MultiItem, registries: Provider): ItemStack =
			this.rItemOutputs[0].copyWithCount(this.rItemOutputs[0].count)

		/**
		 * @return a copy of the first item in [rItemOutputs]
		 */
		override fun getResultItem(registries: Provider): ItemStack = this.rItemOutputs[0].copy()
		fun assembleItems(): List<ItemStack> = buildList {
			repeat(this@MultiItem.rItemOutputs.size) { index ->
				this.add(
					this@MultiItem.rItemOutputs[index].copyWithCount(this@MultiItem.rItemOutputs[index].count)
				)
			}
		}

		fun consumeInputs(items: List<ItemStack>) {
			val list: MutableList<ItemStack> = mutableListOf()
			this.rItemInputs.forEach { list.add(items.find(it::test) ?: return@forEach) }
			list.forEach { item -> this.rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
		}

		fun inputStillValid(items: List<ItemStack>): Boolean =
			this.rItemInputs.all { rItem -> items.any(rItem::test) }

		fun canFitResults(list: List<ItemStack>): Boolean =
			list.all { iItem ->
				this.rItemOutputs.any { rItem ->
					iItem.count < rItem.maxStackSize || iItem.count + rItem.count < rItem.maxStackSize
				}
			}
	}

	abstract class SingleFluidItem(
		val rFluidInput: SizedFluidIngredient,
		val rItemInput: SizedIngredient,
		val rItemOutput: ItemStack,
		val rFluidOutput: FluidStack,
		rTime: Int?,
		rEnergy: Int?
	) : BreadModRecipes<org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs.SingleFluidItem>(rTime, rEnergy) {
		override fun matches(input: BMRecipeInputs.SingleFluidItem, level: Level): Boolean =
			this.rFluidInput.test(input.iFluid) && this.rItemInput.test(input.iItem) && super.matches(input, level)

		override fun assemble(input: BMRecipeInputs.SingleFluidItem, registries: Provider): ItemStack =
			this.rItemOutput.copyWithCount(this.rItemOutput.count)

		fun assembleOutputs(): Pair<FluidStack, ItemStack> =
			this.rFluidOutput.copyWithAmount(this.rFluidOutput.amount) to this.rItemOutput.copyWithCount(this.rItemOutput.count)

		fun inputStillValid(item: ItemStack, fluids: FluidStack): Boolean =
			this.rItemInput.test(item) && this.rFluidInput.test(fluids)

		fun canFitResults(
			tank: ExpansibleTank,
			items: List<ItemStack>,
			itemIndex: Int
		): Boolean =
			tank.amount < tank.capacity || tank.amount + this.rFluidOutput.amount.toBigDecimal() < tank.capacity
					&& (items[itemIndex].count < items[itemIndex].maxStackSize ||
					items[itemIndex].count + this.rItemOutput.count < items[itemIndex].maxStackSize)

		fun consumeInputs(tank: ExpansibleTank, items: List<ItemStack>, itemIndex: Int) {
			tank.drain(this.rFluidInput.amount(), EXECUTE)
			items[itemIndex].shrink(this.rItemInput.count())
		}

		override fun getResultItem(registries: Provider): ItemStack = this.rItemOutput.copy()
		override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1
	}
}