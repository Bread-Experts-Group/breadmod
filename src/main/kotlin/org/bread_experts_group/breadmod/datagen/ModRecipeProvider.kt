package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SpecialRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipeBuilder
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.experimental.recipe.multi.MultiItemTestRecipe
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        WheatCrusherRecipeBuilder(ModItems.FLOUR.toStack(), 2)
            .timeRequired(100)
            .energyRequired(2000)
            .itemRequired(Items.WHEAT, 1)
            .save(recipeOutput, modLocation("special", "machine", "wheat_crushing"))

//        DoughMachineRecipeBuilder(ModItems.TEST_BREAD.toStack(), 1)
//            .itemRequired(ModItems.FLOUR.get(), 2)
//            .timeRequired(100)
//            .energyRequired(5000)
//            .save(recipeOutput, modLocation("special", "machine", "dough_testing"))

//        FluidEnergyRecipeBuilder(
//            listOf(ItemStack(ModItems.TOOL_GUN.asItem(), 2), ItemStack(Items.BREAD, 16)),
//            listOf(FluidStack(Fluids.LAVA, 6000))
//        )
//            .requiresItem(Items.WHEAT.defaultInstance, 5)
//            .requiresItem(ItemTags.BEDS)
//            .requiresItem(ItemTags.DIRT, 50)
//            .requiresFluid(Fluids.LAVA, 500)
//            .timeRequired(100)
//            .energyRequired(4000)
//            .save(recipeOutput, modLocation("special", "hell"))

//        FluidEnergyRecipeBuilder(listOf(Items.BREAD.defaultInstance))
//            .requiresItem(ModItems.FLOUR.toStack())
//            .timeRequired(100)
//            .energyRequired(1000)
//            .save(recipeOutput, modLocation("special", "machine", "bread_test"))

        ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.TEST_BREAD.toStack())
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD, 5)
            .save(recipeOutput, modLocation("special", "test"))

        SpecialRecipeBuilder.special { BreadSlicingRecipe() }
            .save(recipeOutput, modLocation("special", "crafting", "bread_slicing"))

        // Exp

        MultiItemTestRecipe.Builder(listOf(Items.BREAD to 5))
            .itemRequired(ModItems.FLOUR.get(), 3)
            .itemRequired(ItemTags.BEDS, 1)
            .timeRequired(100)
            .save(recipeOutput, modLocation("experimental", "multi_item_test"))

        // todo make DoughMachineRecipeBuilder
    }
}