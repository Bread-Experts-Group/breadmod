package breadmod.datagen

import breadmod.block.registry.ModBlocks
import breadmod.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.Consumer

class ModRecipe(pOutput: PackOutput) : RecipeProvider(pOutput) {
    override fun buildRecipes(pWriter: Consumer<FinishedRecipe>) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK.get())
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD, 9)
            .save(pWriter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BREAD_SLICE.get(), 6)
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD)
            .requires(ItemTags.SWORDS)
            .save(pWriter)

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SHIELD.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('I', Items.IRON_INGOT)

            .pattern("BIB")
            .pattern("BBB")
            .pattern(" B ")
            .save(pWriter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(Items.CHARCOAL))
            .define('C', Items.CHARCOAL)
            .pattern("CC")
            .pattern("CC")
            .save(pWriter, "charcoal_low_compaction")
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get(), 4)
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .define('C', ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
            .pattern("CCC")
            .pattern("CCC")
            .pattern("CCC")
            .save(pWriter, "ld_charcoal_compaction_9")
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(Items.CHARCOAL))
            .define('C', Items.CHARCOAL)
            .pattern("CCC")
            .pattern("CCC")
            .pattern("CCC")
            .save(pWriter, "charcoal_compaction")
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .requires(Items.CHARCOAL, 1)
            .requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 2)
            .save(pWriter, "ld_charcoal_compaction")
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 4)
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 1)
            .save(pWriter, "ld_charcoal_decompaction")
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 9)
            .unlockedBy("has_item", has(ModBlocks.CHARCOAL_BLOCK.get()))
            .requires(ModBlocks.CHARCOAL_BLOCK.get(), 1)
            .save(pWriter, "charcoal_decompaction")
        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModBlocks.BREAD_BLOCK.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.BUILDING_BLOCKS,
            ModBlocks.REINFORCED_BREAD_BLOCK.get()
        ).unlocks("has_item", has(ModBlocks.BREAD_BLOCK.get())).save(pWriter, "reinforced_bread_block_smithing")
    }
}