package org.bread_experts_group.breadmod.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.registry.ModBlocks
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    packOutput: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(packOutput, registries) {
    override fun buildRecipes(p0: RecipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK)
            .requires(Items.BREAD, 9)
            .unlockedBy("has_bread", has(Items.BREAD))
            .save(p0, Breadmod.modLoc("bread_to_bread_block"))
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BREAD, 9)
            .requires(ModBlocks.BREAD_BLOCK)
            .unlockedBy("has_bread_block", has(ModBlocks.BREAD_BLOCK))
            .save(p0, Breadmod.modLoc( "bread_block_to_bread"))
    }
}