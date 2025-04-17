package org.bread_experts_group.breadmod.experimental.recipe.recipe.multi

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
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
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.registry.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class MultiFluidTestRecipe(
	rFluidInputs: NonNullList<SizedFluidIngredient>,
	rFluidOutputs: List<FluidStack>,
	rTime: Int?,
	rEnergy: Int?
) : BreadModRecipes.MultiFluid(rFluidInputs, rFluidOutputs, rTime, rEnergy) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.MULTI_FLUID_TEST.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.MULTI_FLUID.get()
	class Serializer : BMRecipeSerializer<MultiFluidTestRecipe>() {
		override fun codec(): MapCodec<MultiFluidTestRecipe> = RecordCodecBuilder.mapCodec { inst ->
			inst.group(
				this.sizedFluidIngredientCodecModule("ingredients", MultiFluidTestRecipe::rFluidInputs),
				this.fluidStackListCodecModule("results", MultiFluidTestRecipe::rFluidOutputs),
				this.optionalIntCodecModule("time", MultiFluidTestRecipe::rTime),
				this.optionalIntCodecModule("energy", MultiFluidTestRecipe::rEnergy)
			).apply(inst, ::MultiFluidTestRecipe)
		}

		override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MultiFluidTestRecipe> = StreamCodec.composite(
			this.nonNullListStreamCodec(SizedFluidIngredient.STREAM_CODEC), MultiFluidTestRecipe::rFluidInputs,
			this.listStreamCodec(FluidStack.STREAM_CODEC), MultiFluidTestRecipe::rFluidOutputs,
			ByteBufCodecs.INT, MultiFluidTestRecipe::rTime,
			ByteBufCodecs.INT, MultiFluidTestRecipe::rEnergy,
			::MultiFluidTestRecipe
		)
	}

	class Builder(
		private val results: List<Pair<Fluid, Int>>
	) : BMRecipeBuilder.Multi() {
		override fun getResult(): Item = ItemStack.EMPTY.item
		override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
			val recipe = MultiFluidTestRecipe(
				this.fluids,
				buildList { this@Builder.results.forEach { this.add(FluidStack(it.first, it.second)) } },
				this.time,
				this.energy
			)
			recipeOutput.accept(id, recipe, this.buildAdvancement(recipeOutput, id))
		}
	}
}