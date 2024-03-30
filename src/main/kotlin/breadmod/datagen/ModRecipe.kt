package breadmod.datagen

import breadmod.block.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Items
import java.util.function.Consumer

class ModRecipe(pOutput: PackOutput?) : RecipeProvider(pOutput!!) {
    override fun buildRecipes(pWriter: Consumer<FinishedRecipe>) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK)
            .requires(Items.BREAD, 9)
            .unlockedBy("has_item", has(Items.BREAD))
            .save(pWriter)
    }
}