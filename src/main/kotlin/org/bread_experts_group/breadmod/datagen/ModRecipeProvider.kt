package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipeBuilder
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        WheatCrusherRecipeBuilder(ModItems.FLOUR.toStack(), 2)
            .timeRequired(100)
            .energyRequired(2000)
            .itemRequired(Items.WHEAT)
            .save(recipeOutput, modLocation("special", "machine", "wheat_crushing"))

        ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.TEST_BREAD.toStack())
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD, 5)
            .save(recipeOutput, modLocation("special", "test"))

        SpecialRecipeBuilder.special { BreadSlicingRecipe() }
            .save(recipeOutput, modLocation("special", "crafting", "bread_slicing"))

        // Exp

        MultiItemTestRecipe.Builder(listOf(Items.BREAD to 5))
            .itemRequired(ModItems.FLOUR.get(), 3)
            .itemRequired(ItemTags.BEDS)
            .timeRequired(100)
            .save(recipeOutput, modLocation("experimental", "multi_item_test"))
        MultiItemTestRecipe.Builder(listOf(ModItems.TOOL_GUN.get() to 1))
            .itemRequired(ModItems.TOASTER_HEATING_ELEMENT.get())
            .itemRequired(ItemTags.HOES)
            .itemRequired(ItemTags.ANVIL, 3)
            .timeRequired(50)
            .save(recipeOutput, modLocation("experimental", "multi_item_test_two"))

        SingleItemTestRecipe.Builder(ModItems.FLOUR.get(), 10)
            .itemRequired(Items.BREAD, 5)
            .timeRequired(50)
            .save(recipeOutput, modLocation("experimental", "single_item_test"))
        SingleItemTestRecipe.Builder(Items.COD, 1)
            .itemRequired(Items.PUFFERFISH)
            .timeRequired(100)
            .save(recipeOutput, modLocation("experimental", "single_item_test_two"))

        SingleFluidTestRecipe.Builder(ModFluids.BREAD_LIQUID.source.get(), 1000)
            .fluidRequired(Fluids.WATER, 500)
            .timeRequired(100)
            .save(recipeOutput, modLocation("experimental", "single_fluid_test"))
    }
}