package org.bread_experts_group.breadmod.experimental.recipe.single

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class SingleFluidTestRecipe(
    rFluidInput: SizedFluidIngredient,
    rFluidOutput: FluidStack,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.SingleFluid(rFluidInput, rFluidOutput, rTime, rEnergy) {
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.SINGLE_FLUID_TEST.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.SINGLE_FLUID.get()

    override fun isIncomplete(): Boolean = false

    class Serializer : BMRecipeSerializer<SingleFluidTestRecipe>() {
        override fun codec(): MapCodec<SingleFluidTestRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                SizedFluidIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(SingleFluidTestRecipe::rFluidInput),
                FluidStack.CODEC.fieldOf("result").forGetter(SingleFluidTestRecipe::rFluidOutput),
                optionalIntCodecModule("time", SingleFluidTestRecipe::rTime),
                optionalIntCodecModule("energy", SingleFluidTestRecipe::rEnergy)
            ).apply(inst, ::SingleFluidTestRecipe)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SingleFluidTestRecipe> = StreamCodec.composite(
            SizedFluidIngredient.STREAM_CODEC, SingleFluidTestRecipe::rFluidInput,
            FluidStack.STREAM_CODEC, SingleFluidTestRecipe::rFluidOutput,
            ByteBufCodecs.INT, SingleFluidTestRecipe::rTime,
            ByteBufCodecs.INT, SingleFluidTestRecipe::rEnergy,
            ::SingleFluidTestRecipe
        )
    }

    class Builder(
        private val result: Fluid,
        private val amount: Int
    ) : BMRecipeBuilder() {
        private var fluid = SizedFluidIngredient.of(Fluids.WATER, 1)

        override fun getResult(): Item = ItemStack.EMPTY.item
        fun getFluid(): Fluid = result

        fun fluidRequired(fluid: Fluid, amount: Int = 1000) =
            this.also { this.fluid = SizedFluidIngredient.of(fluid, amount) }

        fun fluidRequired(tag: TagKey<Fluid>, amount: Int = 1000) =
            this.also { fluid = SizedFluidIngredient.of(tag, amount) }

        override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
            val recipe = SingleFluidTestRecipe(fluid, FluidStack(result, amount), time, energy)
            recipeOutput.accept(id, recipe, buildAdvancement(recipeOutput, id))
        }
    }
}