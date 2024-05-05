package breadmod.datagen

import breadmod.BreadMod.modLocation
import breadmod.datagen.recipe.compat.create.CreateMixingRecipeBuilder
import breadmod.datagen.recipe.FluidEnergyRecipeBuilder
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers
import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.common.registries.MekanismItems
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.tags.FluidTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.ModList
import java.util.function.Consumer

class ModRecipeProvider(pOutput: PackOutput) : RecipeProvider(pOutput) {
    override fun buildRecipes(pWriter: Consumer<FinishedRecipe>) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK.get())
            .unlockedBy("has_item", has(Items.BREAD))
            .requires(Items.BREAD, 9)
            .save(pWriter, modLocation("building_blocks", "bread_block"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BREAD, 9)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .requires(ModBlocks.BREAD_BLOCK.get(), 1)
            .save(pWriter, modLocation("building_blocks", "bread_block_to_bread"))

        // Built-in function from RecipeProvider, needs .unlockedBy and .save to work properly
//        buttonBuilder(Items.BIRCH_BUTTON, Ingredient.of(ModItems.FLOUR.get()))
//            .unlockedBy("has_item", has(ModItems.FLOUR.get()))
//            .save(pWriter, modLocation("button_test"))
//        threeByThreePacker(pWriter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BREAD_BLOCK.get(), Items.BREAD)

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SHIELD.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('I', Items.IRON_INGOT)
            .pattern("BIB")
            .pattern("BBB")
            .pattern(" B ")
            .save(pWriter, modLocation("combat", "bread_shield"))

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(Items.CHARCOAL))
            .define('C', Items.CHARCOAL)
            .pattern("CC")
            .pattern("CC")
            .save(pWriter, modLocation("building_blocks", "charcoal_low_compaction"))

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get(), 4)
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .define('C', ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
            .pattern("CCC")
            .pattern("CCC")
            .pattern("CCC")
            .save(pWriter, modLocation("building_blocks", "ld_charcoal_compaction_9"))

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(Items.CHARCOAL))
            .define('C', Items.CHARCOAL)
            .pattern("CCC")
            .pattern("CCC")
            .pattern("CCC")
            .save(pWriter, modLocation("building_blocks", "charcoal_compaction"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .requires(Items.CHARCOAL, 1)
            .requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 2)
            .save(pWriter, modLocation("building_blocks", "ld_charcoal_compaction"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 4)
            .unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
            .requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 1)
            .save(pWriter, modLocation("building_blocks", "ld_charcoal_decompaction"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 9)
            .unlockedBy("has_item", has(ModBlocks.CHARCOAL_BLOCK.get()))
            .requires(ModBlocks.CHARCOAL_BLOCK.get(), 1)
            .save(pWriter, modLocation("building_blocks", "charcoal_decompaction"))

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEATING_ELEMENT_BLOCK.get())
            .unlockedBy("has_item", has(Items.COPPER_INGOT)) // TODO: Aluminum
            .define('C', Items.COPPER_INGOT) // TODO: Aluminum String
            .define('S', Items.STICK) // TODO: Metal rod
            .pattern("CCC")
            .pattern("CSC")
            .pattern("CCC")
            .save(pWriter, modLocation("heating_element"))

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModBlocks.FLOUR_BLOCK.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
            .define('F', ModItems.FLOUR.get())
            .pattern("FF ")
            .pattern("FF ")
            .pattern("   ")
            .save((pWriter), modLocation("food", "flour_block_from_flour"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 4)
            .unlockedBy("has_item", has(ModItems.FLOUR.get()))
            .requires(ModBlocks.FLOUR_BLOCK.get(), 1)
            .save((pWriter), modLocation("food", "flour_from_flour_block"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 1)
            .unlockedBy("has_item", has(Items.WHEAT)) // TODO: Mortar and Pestle for crushing wheat into flour
            .requires(Items.WHEAT, 1)
            .save((pWriter), modLocation("food", "flour_from_wheat"))

        SimpleCookingRecipeBuilder.smoking(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            100
        ).unlockedBy("has_item", has(ModItems.DOUGH.get()))
            .save(pWriter, modLocation("food", "bread_from_smoking"))

        SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            600
        ).unlockedBy("has_item", has(ModItems.DOUGH.get()))
            .save(pWriter, modLocation("food", "bread_from_campfire_cooking"))

        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModItems.DOUGH.get()),
            RecipeCategory.FOOD,
            Items.BREAD,
            0f,
            200
        ).unlockedBy("has_item", has(ModItems.DOUGH.get()))
            .save(pWriter, modLocation("food", "bread_from_smelting"))

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.DOUGH.get(), 8)
            .unlockedBy("has_item", has(Items.WHEAT))
            .define('F', ModItems.FLOUR.get())
            .define('B', Items.WATER_BUCKET)
            .pattern("FFF")
            .pattern("FBF")
            .pattern("FFF")
            .save(pWriter, modLocation("food", "dough"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModBlocks.BREAD_BLOCK.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.BUILDING_BLOCKS,
            ModBlocks.REINFORCED_BREAD_BLOCK.get()
        )
            .unlocks("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_block_smithing"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModItems.BREAD_SWORD.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.COMBAT,
            ModItems.RF_BREAD_SWORD.get()
        )
            .unlocks("has_item", has(ModItems.BREAD_SWORD.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_sword_smithing"))

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SWORD.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern(" B ")
            .pattern(" B ")
            .pattern(" S ")
            .save(pWriter, modLocation("combat", "bread_sword"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModItems.BREAD_SHOVEL.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.TOOLS,
            ModItems.RF_BREAD_SHOVEL.get()
        )
            .unlocks("has_item", has(ModItems.BREAD_SHOVEL.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_shovel_smithing"))

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_SHOVEL.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern(" B ")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_shovel"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModItems.BREAD_AXE.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.TOOLS,
            ModItems.RF_BREAD_AXE.get()
        )
            .unlocks("has_item", has(ModItems.BREAD_AXE.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_axe_smithing"))

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_AXE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BB ")
            .pattern("BS ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_axe"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModItems.BREAD_PICKAXE.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.TOOLS,
            ModItems.RF_BREAD_PICKAXE.get()
        )
            .unlocks("has_item", has(ModItems.BREAD_PICKAXE.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_pickaxe_smithing"))

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_PICKAXE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BBB")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_pickaxe"))

        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(ModItems.BREAD_HOE.get()),
            Ingredient.of(Items.NETHERITE_INGOT),
            RecipeCategory.TOOLS,
            ModItems.RF_BREAD_HOE.get()
        )
            .unlocks("has_item", has(ModItems.BREAD_PICKAXE.get()))
            .save(pWriter, modLocation("smithing", "reinforced_bread_hoe_smithing"))

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_HOE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BB ")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_hoe"))

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLOUR_LAYER_BLOCK.get(), 6)
            .unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
            .define('F', ModBlocks.FLOUR_BLOCK.get())
            .pattern("   ")
            .pattern("FFF")
            .pattern("   ")
            .save(pWriter, modLocation("building_blocks", "flour_layer_block"))

        SpecialRecipeBuilder.special(ModRecipeSerializers.ARMOR_POTION.get())
            .save(pWriter, modLocation("special", "crafting", "armor_potion_doping").toString())
        SpecialRecipeBuilder.special(ModRecipeSerializers.BREAD_SLICE.get())
            .save(pWriter, modLocation("special", "crafting", "bread_slicing").toString())
        SpecialRecipeBuilder.special(ModRecipeSerializers.DOPED_BREAD.get())
            .save(pWriter, modLocation("special", "crafting", "bread_potion_doping").toString())

        FluidEnergyRecipeBuilder(listOf(ModItems.DOUGH.get().defaultInstance))
            .setSerializer(ModRecipeSerializers.FLOUR_TO_DOUGH.get())
            .setTimeRequired(20 * 5)
            .setRFRequired(500)
            .requiresItem(ModItems.FLOUR.get())
            .requiresFluid(FluidTags.WATER, 250)
            .save(pWriter, modLocation("special", "machine", "flour_to_dough"))

        // // Compat
        // Create
        if(ModList.get().isLoaded("create")) {
            CreateMixingRecipeBuilder(ItemStack(ModBlocks.BREAD_BLOCK.get(), 2))
                .heatRequirement(CreateMixingRecipeBuilder.HeatRequirement.HEATED)
                .requiresItem(Items.BREAD, 1)
                .requiresFluid(FluidTags.WATER, 1000)
                .save(pWriter, modLocation("mixing", "bread_mixing_test"))
        }

        // Mekanism
        if(ModList.get().isLoaded("mekanism")) {
            ItemStackToItemStackRecipeBuilder.crushing(
                IngredientCreatorAccess.item().from(ModBlocks.BREAD_BLOCK.get()),
                ItemStack(MekanismItems.BIO_FUEL.get(), 63)
            )
        }
    }
}