package breadmod.datagen

import breadmod.block.registry.ModBlocks
import breadmod.item.registry.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BREAD_SLICE.get(), 8)
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD)
            .requires(ItemTags.SWORDS) // TODO: Fix this to not consume the entire sword when crafting
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

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEATING_ELEMENT_BLOCK.get())
            .unlockedBy("has_item", has(Items.COPPER_INGOT)) // TODO: Aluminum
            .define('C', Items.COPPER_INGOT) // TODO: Aluminum String
            .define('S', Items.STICK) // TODO: Metal rod
            .pattern("CCC")
            .pattern("CSC")
            .pattern("CCC")
            .save(pWriter)
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModBlocks.FLOUR_BLOCK.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
            .define('F', ModItems.FLOUR.get())
            .pattern("FF ")
            .pattern("FF ")
            .pattern("   ")
            .save((pWriter), "flour_block_from_flour")
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 4)
            .unlockedBy("has_item", has(ModItems.FLOUR.get()))
            .requires(ModBlocks.FLOUR_BLOCK.get(), 1)
            .save((pWriter), "flour_from_flour_block")
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 1)
            .unlockedBy("has_item", has(Items.WHEAT)) // TODO: Mortar and Pestle for crushing wheat into flour
            .requires(Items.WHEAT, 1)
            .save((pWriter), "flour_from_wheat")
        SimpleCookingRecipeBuilder.smoking(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            100
        ).unlockedBy("has_item", has(ModItems.DOUGH.get())).save(pWriter, "bread_from_smoking")
        SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            600
        ).unlockedBy("has_item", has(ModItems.DOUGH.get())).save(pWriter, "bread_from_campfire_cooking")
        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            200
        ).unlockedBy("has_item", has(ModItems.DOUGH.get())).save(pWriter, "bread_from_smelting")

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.DOUGH.get(), 8)
            .unlockedBy("has_item", has(Items.WHEAT))
            .define('F', ModItems.FLOUR.get())
            .define('B', Items.WATER_BUCKET)
            .pattern("FFF")
            .pattern("FBF")
            .pattern("FFF")
            .save(pWriter)

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModBlocks.BREAD_BLOCK.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.BUILDING_BLOCKS,
            ModBlocks.REINFORCED_BREAD_BLOCK.get()
        ).unlocks("has_item", has(ModBlocks.BREAD_BLOCK.get())).save(pWriter, "reinforced_bread_block_smithing")
    }
}