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
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.common.crafting.SizedIngredient
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

@Suppress("unused")
class SingleItemTestRecipe(
    rItemInput: SizedIngredient,
    rItemOutput: ItemStack,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.SingleItem(rItemInput, rItemOutput, rTime, rEnergy) {
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height == 1
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.SINGLE_ITEM_TEST.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.SINGLE_ITEM.get()

    class Serializer : BMRecipeSerializer<SingleItemTestRecipe>() {
        override fun codec(): MapCodec<SingleItemTestRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(SingleItemTestRecipe::rItemInput),
                ItemStack.CODEC.fieldOf("result").forGetter(SingleItemTestRecipe::rItemOutput),
                optionalIntCodecModule("time", SingleItemTestRecipe::rTime),
                optionalIntCodecModule("energy", SingleItemTestRecipe::rEnergy)
            ).apply(inst, ::SingleItemTestRecipe)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SingleItemTestRecipe> = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC, SingleItemTestRecipe::rItemInput,
            ItemStack.STREAM_CODEC, SingleItemTestRecipe::rItemOutput,
            ByteBufCodecs.INT, SingleItemTestRecipe::rTime,
            ByteBufCodecs.INT, SingleItemTestRecipe::rEnergy,
            ::SingleItemTestRecipe
        )
    }

    class Builder(
        private val result: Item,
        private val count: Int
    ) : BMRecipeBuilder() {
        private var item = SizedIngredient.of(Items.AIR, 1)

        override fun getResult(): Item = result

        fun itemRequired(item: Item, count: Int = 1) = this.also { this.item = SizedIngredient.of(item, count) }
        fun itemRequired(tag: TagKey<Item>, count: Int = 1) =
            this.also { item = SizedIngredient.of(tag, count) }

        override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
            if (item == SizedIngredient.of(Items.AIR, 1)) throw IllegalArgumentException("ingredient must be set!")
            val recipe = SingleItemTestRecipe(item, ItemStack(result).copyWithCount(count), time, energy)
            recipeOutput.accept(id, recipe, buildAdvancement(recipeOutput, id))
        }

    }
}