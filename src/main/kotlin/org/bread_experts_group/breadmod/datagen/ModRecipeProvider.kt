package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.recipe.fluid_energy.FluidEnergyRecipeBuilder
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipeBuilder
import org.bread_experts_group.breadmod.registry.item.ModItems
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

        FluidEnergyRecipeBuilder(listOf(ModItems.TOOL_GUN.toStack() to 2, Items.BREAD.defaultInstance to 16))
            .requiresItem(Items.WHEAT.defaultInstance)
            .requiresItemTagged(ItemTags.AXES)
            .save(recipeOutput, modLocation("special", "hell"))
    }
}