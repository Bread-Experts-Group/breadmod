package org.bread_experts_group.breadmod.experimental.recipe.single

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.common.crafting.SizedIngredient
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe.BreadModRecipes

@Suppress("unused")
class SingleItemTestRecipe(
    rItemInput: SizedIngredient,
    rItemOutput: ItemStack,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.SingleItem(rItemInput, rItemOutput, rTime, rEnergy) {
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height == 1
    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }

    override fun getType(): RecipeType<*> {
        TODO("Not yet implemented")
    }

    class Serializer : BMRecipeSerializer<SingleItemTestRecipe>() {
        override fun codec(): MapCodec<SingleItemTestRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(SingleItemTestRecipe::rItemInput),
                ItemStack.CODEC.fieldOf("result").forGetter(SingleItemTestRecipe::rItemOutput),
                optionalIntCodecModule("time", SingleItemTestRecipe::rTime),
                optionalIntCodecModule("energy", SingleItemTestRecipe::rEnergy)
            ).apply(inst, ::SingleItemTestRecipe)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SingleItemTestRecipe> {
            TODO("Not yet implemented")
        }
    }
}