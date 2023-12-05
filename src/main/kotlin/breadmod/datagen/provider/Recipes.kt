package breadmod.datagen.provider


import breadmod.item.ModItems.BREAD_BLOCK_ITEM
import breadmod.item.ModItems.BREAD_SHIELD
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
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

        ShapedRecipeBuilder
            .shaped { BREAD_SHIELD }
            .unlockedBy("has_item", has(BREAD_BLOCK_ITEM))
            .define('B', Ingredient.of(BREAD_BLOCK_ITEM))
            .define('I', Ingredient.of(Items.IRON_INGOT))
            .pattern("BIB").pattern("BBB").pattern(" B ")
            .save(pFinishedRecipeConsumer)
    }
}