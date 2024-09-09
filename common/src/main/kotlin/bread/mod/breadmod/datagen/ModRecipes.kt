package bread.mod.breadmod.datagen

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.registry.block.ModBlocks
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider.has
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Items

object ModRecipes {
    fun buildRecipes(output: RecipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK.get())
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD, 9)
            .save(output, modLocation("building_blocks", "bread_to_bread_block"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BREAD, 9)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .requires(ModBlocks.BREAD_BLOCK.get(), 1)
            .save(output, modLocation("building_blocks", "bread_block_to_bread"))
    }
}