package org.bread_experts_group.breadmod.experimental.recipe.multi

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.NonNullList
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
import net.neoforged.neoforge.common.crafting.SizedIngredient
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class MultiItemTestRecipe(
    rItemInputs: NonNullList<SizedIngredient>,
    rItemOutputs: List<ItemStack>,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.MultiItem(rItemInputs, rItemOutputs, rTime, rEnergy) {
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width >= 3 && height >= 1
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.MULTI_ITEM_TEST.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.MULTI_ITEM.get()

    class Serializer : BMRecipeSerializer<MultiItemTestRecipe>() {
        override fun codec(): MapCodec<MultiItemTestRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                sizedIngredientCodecModule("ingredients", MultiItemTestRecipe::rItemInputs),
                itemStackListCodecModule("results", MultiItemTestRecipe::rItemOutputs),
                optionalIntCodecModule("time", MultiItemTestRecipe::rTime),
                optionalIntCodecModule("energy", MultiItemTestRecipe::rEnergy)
            ).apply(inst, ::MultiItemTestRecipe)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MultiItemTestRecipe> = StreamCodec.composite(
            nonNullListStreamCodec(SizedIngredient.STREAM_CODEC), MultiItemTestRecipe::rItemInputs,
            ItemStack.LIST_STREAM_CODEC, MultiItemTestRecipe::rItemOutputs,
            ByteBufCodecs.INT, MultiItemTestRecipe::rTime,
            ByteBufCodecs.INT, MultiItemTestRecipe::rEnergy,
            ::MultiItemTestRecipe
        )
    }

    class Builder(
        private val results: List<Pair<Item, Int>>
    ) : BMRecipeBuilder() {
        private var items = NonNullList.create<SizedIngredient>()

        fun itemRequired(item: Item, count: Int = 1): Builder =
            this.also { this.items.add(SizedIngredient.of(item, count)) }

        fun itemRequired(tag: TagKey<Item>, count: Int = 1): Builder =
            this.also { this.items.add(SizedIngredient.of(tag, count)) }

        override fun getResult(): Item = results[0].first

        override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
            val recipe = MultiItemTestRecipe(
                items,
                buildList { results.forEach { add(ItemStack(it.first, it.second)) } },
                time,
                energy
            )
            recipeOutput.accept(id, recipe, buildAdvancement(recipeOutput, id))
        }
    }
}