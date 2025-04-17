package org.bread_experts_group.breadmod.experimental.recipe.recipe.single

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
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
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.registry.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class SingleFluidItemRecipe(
	rFluidInput: SizedFluidIngredient,
	rItemInput: SizedIngredient,
	rItemOutput: ItemStack,
	rFluidOutput: FluidStack,
	rTime: Int?,
	rEnergy: Int?
) : BreadModRecipes.SingleFluidItem(rFluidInput, rItemInput, rItemOutput, rFluidOutput, rTime, rEnergy) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.SINGLE_FLUID_ITEM.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.SINGLE_FLUID_ITEM.get()
	class Serializer : BMRecipeSerializer<SingleFluidItemRecipe>() {
		override fun codec(): MapCodec<SingleFluidItemRecipe> = RecordCodecBuilder.mapCodec { inst ->
			inst.group(
				SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid_ingredient")
					.forGetter(SingleFluidItemRecipe::rFluidInput),
				SizedIngredient.FLAT_CODEC.fieldOf("item_ingredient").forGetter(SingleFluidItemRecipe::rItemInput),
				ItemStack.CODEC.fieldOf("item_result").forGetter(SingleFluidItemRecipe::rItemOutput),
				FluidStack.CODEC.fieldOf("fluid_result").forGetter(SingleFluidItemRecipe::rFluidOutput),
				this.optionalIntCodecModule("time", SingleFluidItemRecipe::rTime),
				this.optionalIntCodecModule("energy", SingleFluidItemRecipe::rEnergy)
			).apply(inst, ::SingleFluidItemRecipe)
		}

		override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SingleFluidItemRecipe> =
			StreamCodec.composite(
				SizedFluidIngredient.STREAM_CODEC, SingleFluidItemRecipe::rFluidInput,
				SizedIngredient.STREAM_CODEC, SingleFluidItemRecipe::rItemInput,
				ItemStack.STREAM_CODEC, SingleFluidItemRecipe::rItemOutput,
				FluidStack.STREAM_CODEC, SingleFluidItemRecipe::rFluidOutput,
				ByteBufCodecs.INT, SingleFluidItemRecipe::rTime,
				ByteBufCodecs.INT, SingleFluidItemRecipe::rEnergy,
				::SingleFluidItemRecipe
			)
	}

	class Builder(
		private val itemResult: Pair<Item, Int>,
		private val fluidResult: Pair<Fluid, Int>
	) : BMRecipeBuilder.Single() {
		override fun getResult(): Item = this.itemResult.first
		override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
			val recipe = SingleFluidItemRecipe(
				this.fluid,
				this.item,
				ItemStack(this.itemResult.first).copyWithCount(this.itemResult.second),
				FluidStack(this.fluidResult.first, this.fluidResult.second),
				this.time,
				this.energy
			)
			recipeOutput.accept(id, recipe, this.buildAdvancement(recipeOutput, id))
		}
	}
}