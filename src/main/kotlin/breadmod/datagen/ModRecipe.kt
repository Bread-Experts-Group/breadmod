package breadmod.datagen

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.item.ModItems
import breadmod.item.armor.ArmorPotionRecipe
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Consumer

class ModRecipe(pOutput: PackOutput?) : RecipeProvider(pOutput!!) {

    override fun buildRecipes(pWriter: Consumer<FinishedRecipe>) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK)
            .requires(Items.BREAD, 9)
            .unlockedBy("has_item", has(Items.BREAD))
            .save(pWriter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BREAD_SLICE, 6)
            .requires(Items.BREAD)
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(ItemTags.SWORDS)
            .save(pWriter)

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SHIELD)
            .unlockedBy("has_item", has(ModItems.BREAD_BLOCK_ITEM))
            .define('B', Ingredient.of(ModItems.BREAD_BLOCK_ITEM))
            .define('I', Items.IRON_INGOT)
            .pattern("BIB").pattern("BBB").pattern(" B ")
            .save(pWriter)
    }
}