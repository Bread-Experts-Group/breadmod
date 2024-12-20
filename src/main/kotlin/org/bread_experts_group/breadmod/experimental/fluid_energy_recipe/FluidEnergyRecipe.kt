package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe

import com.mojang.datafixers.util.Function6
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTank
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BreadModRecipes

typealias RecipeFunction = (
	NonNullList<SizedIngredient>,
	MutableList<ItemStack>,
	NonNullList<SizedFluidIngredient>,
	MutableList<FluidStack>,
	Int?,
	Int?
) -> FluidEnergyRecipe

typealias RecipeFunctionDataFixer<R> = Function6<
		NonNullList<SizedIngredient>,
		MutableList<ItemStack>,
		NonNullList<SizedFluidIngredient>,
		MutableList<FluidStack>,
		Int?,
		Int?,
		R
		>

abstract class FluidEnergyRecipe(
	val rItemInputs : NonNullList<SizedIngredient>,
	val rItemOutputs : MutableList<ItemStack>,
	val rFluidInputs : NonNullList<SizedFluidIngredient>,
	val rFluidOutputs : MutableList<FluidStack>,
	rTime : Int?,
	rEnergy : Int?
) : BreadModRecipes<FluidEnergyRecipe.FluidEnergyInput>(rTime, rEnergy) {
	override fun matches(input : FluidEnergyInput, level : Level) : Boolean =
		this.rItemInputs.all { rItem ->
			input.iItems.any(rItem::test)
		} && this.rFluidInputs.all { rFluid ->
			input.iFluids.any(rFluid::test)
		} && super.matches(input, level)

	override fun assemble(input : FluidEnergyInput, registries : HolderLookup.Provider) : ItemStack =
		this.rItemOutputs[0]

	fun assembleOutputs(input : FluidEnergyInput) : Pair<List<ItemStack>, List<FluidStack>> =
		buildList {
			repeat(this@FluidEnergyRecipe.rItemOutputs.size) { index ->
				this.add(this@FluidEnergyRecipe.rItemOutputs[index].copyWithCount(input.iCount[index]))
			}
		} to buildList {
			repeat(this@FluidEnergyRecipe.rFluidOutputs.size) { index ->
				this.add(this@FluidEnergyRecipe.rFluidOutputs[index].copyWithAmount(input.iAmount[index]))
			}
		}

	override fun canCraftInDimensions(width : Int, height : Int) : Boolean = width * height >= 1
	override fun getResultItem(registries : HolderLookup.Provider) : ItemStack = this.rItemOutputs[0].copy()
	fun consumeInputs(items : List<ItemStack>, fluids : List<FluidStack>) {
		val itemList : MutableList<ItemStack> = mutableListOf()
		val fluidList : MutableList<FluidStack> = mutableListOf()
		this.rItemInputs.forEach { itemList.add(items.find(it::test) ?: return@forEach) }
		this.rItemInputs.forEach { itemList.add(items.find(it::test) ?: return@forEach) }
		this.rFluidInputs.forEach { fluidList.add(fluids.find(it::test) ?: return@forEach) }
		itemList.forEach { item -> this.rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
		fluidList.forEach { fluid -> this.rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
	}

	fun inputStillValid(items : List<ItemStack>, fluids : List<FluidStack>) : Boolean =
		this.rItemInputs.all { rItem -> items.any(rItem::test) } &&
				this.rFluidInputs.all { rFluid -> fluids.any(rFluid::test) }
	// todo always returning false, needs rewriting
	//  (maybe just provide a list of FluidStacks instead of the entire tank itself)
	//  the && in the function is what's causing it to always return false, expecting BOTH items and fluids to be true
	//  when the recipe could only use items or fluids
	fun canFitResults(items : List<ItemStack>, tank : SidedFluidTank, fluidCapacity : Int) : Boolean =
		items.all { iItem ->
			this.rItemOutputs.any { rItem ->
				iItem.count < rItem.maxStackSize || iItem.count + rItem.count < rItem.maxStackSize
			}
		} || tank.tanks.all { iFluidHandler ->
			this.rFluidOutputs.any { rFluid ->
				iFluidHandler.fluid.amount < fluidCapacity || iFluidHandler.fluid.amount + rFluid.amount < fluidCapacity
			}
		}

	abstract override fun getSerializer() : RecipeSerializer<*>
	abstract override fun getType() : RecipeType<*>
	class FluidEnergyInput(
		val iItems : List<ItemStack>,
		val iCount : List<Int>,
		val iFluids : List<FluidStack>,
		val iAmount : List<Int>,
		iSize : Int
	) : BMRecipeInputs(iSize) {
		override fun getItem(index : Int) : ItemStack = this.iItems[index]
		override fun isEmpty() : Boolean = (super.isEmpty() || this.iItems.isEmpty()) && this.iFluids.isEmpty()
	}
	// todo figure out how to implement the codec methods as constructor parameters
	//  so we can just instantiate this class
	//  instead of extending it for each recipe
	class FluidEnergySerializer<R : FluidEnergyRecipe>(
		val recipe : RecipeFunctionDataFixer<R>
	) : BMRecipeSerializer<R>() {
		override fun codec() : MapCodec<R> = RecordCodecBuilder.mapCodec { inst ->
			inst.group(
				this.optionalSizedIngredientCodecModule("item_ingredients", FluidEnergyRecipe::rItemInputs),
				this.optionalItemStackListCodecModule("item_results", FluidEnergyRecipe::rItemOutputs),
				this.optionalSizedFluidIngredientCodecModule("fluid_ingredients", FluidEnergyRecipe::rFluidInputs),
				this.optionalFluidStackListCodecModule("fluid_results", FluidEnergyRecipe::rFluidOutputs),
				this.optionalIntCodecModule("time", FluidEnergyRecipe::rTime),
				this.optionalIntCodecModule("energy", FluidEnergyRecipe::rEnergy),
			).apply(inst, this.recipe)
		}

		override fun streamCodec() : StreamCodec<RegistryFriendlyByteBuf, R> =
			StreamCodec.composite(
				this.nonNullListStreamCodec(SizedIngredient.STREAM_CODEC), FluidEnergyRecipe::rItemInputs,
				ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::rItemOutputs,
				this.nonNullListStreamCodec(SizedFluidIngredient.STREAM_CODEC), FluidEnergyRecipe::rFluidInputs,
				FluidStack.STREAM_CODEC.toMutableList(), FluidEnergyRecipe::rFluidOutputs,
				ByteBufCodecs.INT, FluidEnergyRecipe::rTime,
				ByteBufCodecs.INT, FluidEnergyRecipe::rEnergy,
				this.recipe
			)
	}

	class FluidEnergyBuilder(
		private val recipe : RecipeFunction,
		private val itemResults : List<Pair<Item, Int>> = listOf(),
		private val fluidResults : List<Pair<Fluid, Int>> = listOf()
	) : BMRecipeBuilder.Multi() {
		override fun getResult() : Item = this.itemResults[0].first
		override fun save(recipeOutput : RecipeOutput, id : ResourceLocation) {
			recipeOutput.accept(
				id,
				this.recipe.invoke(
					this.items,
					buildList {
						this@FluidEnergyBuilder.itemResults.forEach {
							this.add(
								ItemStack(
									it.first,
									it.second
								)
							)
						}
					}.toMutableList(),
					this.fluids,
					buildList {
						this@FluidEnergyBuilder.fluidResults.forEach {
							this.add(
								FluidStack(
									it.first,
									it.second
								)
							)
						}
					}.toMutableList(),
					this.time,
					this.energy
				), this.buildAdvancement(recipeOutput, id)
			)
		}
	}
}