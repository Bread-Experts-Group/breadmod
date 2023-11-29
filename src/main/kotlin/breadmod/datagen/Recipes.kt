package breadmod.datagen


import breadmod.item.ModItems.BREAD_BLOCK_ITEM
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

class Recipes(generator: DataGenerator) : RecipeProvider(generator) {
    override fun buildCraftingRecipes(pFinishedRecipeConsumer: Consumer<FinishedRecipe>) {
        val bread = ForgeRegistries.ITEMS.getValue(ResourceLocation("minecraft", "bread"))!!

        ShapelessRecipeBuilder
            .shapeless { BREAD_BLOCK_ITEM }
            .requires({ bread }, 9)
            .unlockedBy("has_item", has(bread))
            .save(pFinishedRecipeConsumer)
    }
}