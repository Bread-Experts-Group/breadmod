package breadmod.datagen

import breadmod.ModMain.modLocation
import breadmod.datagen.recipe.compat.create.CreateMixingRecipeBuilder
import breadmod.datagen.recipe.FluidEnergyRecipeBuilder
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers
import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.common.registries.MekanismItems
import moze_intel.projecte.gameObjs.registries.PEItems
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.tags.FluidTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fml.ModList
import java.util.function.Consumer

class ModRecipeProvider(pOutput: PackOutput) : RecipeProvider(pOutput) {
    private fun modNetheriteSmithing(
        pFinishedRecipeConsumer: Consumer<FinishedRecipe>,
        pRecipeLocation: ResourceLocation,
        pIngredient: Item,
        pResult: Item,
        pCategory: RecipeCategory,
        pUnlockItem: Item
    ) {
        SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Ingredient.of(pIngredient),
            Ingredient.of(Items.NETHERITE_INGOT),
            pCategory,
            pResult
        ).unlocks("has_item", has(pUnlockItem))
            .save(pFinishedRecipeConsumer, pRecipeLocation)
    }

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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DOUGH_MACHINE_BLOCK.get())
            .unlockedBy("has_item", has(ModItems.FLOUR.get()))
            .define('I', Items.IRON_INGOT)
            .define('C', Items.COPPER_INGOT)
            .define('R', Items.REDSTONE)
            .define('B', Items.CAULDRON)
            .define('F', Items.FURNACE)
            .pattern("IFI")
            .pattern("CBC")
            .pattern("IRI")
            .save(pWriter, modLocation("misc", "dough_machine"))

        // Amulets
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_BREAD_AMULET.get())
            .unlockedBy("has_item", has(Items.GOLDEN_APPLE))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STRING)
            .define('A', Items.GOLDEN_APPLE)
            .pattern("SBS")
            .pattern("BAB")
            .pattern(" B ")
            .save(pWriter, modLocation("misc", "bread_amulet"))
        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_amulet"),
            ModItems.BASIC_BREAD_AMULET.get(),
            ModItems.REINFORCED_BREAD_AMULET.get(),
            RecipeCategory.MISC,
            ModItems.BASIC_BREAD_AMULET.get())
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INDESTRUCTIBLE_BREAD_AMULET.get())
            .unlockedBy("has_item", has(ModItems.REINFORCED_BREAD_AMULET.get()))
            .define('S', Items.NETHER_STAR)
            .define('R', ModBlocks.REINFORCED_BREAD_BLOCK.get())
            .define('A', ModItems.REINFORCED_BREAD_AMULET.get())
            .define('D', Items.DIAMOND)
            .pattern(" D ")
            .pattern("RAR")
            .pattern(" S ")
            .save(pWriter, modLocation("misc", "indestructible_bread_amulet"))

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

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_block_smithing"),
            ModBlocks.BREAD_BLOCK.get(),
            ModBlocks.REINFORCED_BREAD_BLOCK.get(),
            RecipeCategory.BUILDING_BLOCKS,
            ModBlocks.BREAD_BLOCK.get())

        // Bread Sword
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SWORD.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern(" B ")
            .pattern(" B ")
            .pattern(" S ")
            .save(pWriter, modLocation("combat", "bread_sword"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_sword_smithing"),
            ModItems.BREAD_SWORD.get(),
            ModItems.RF_BREAD_SWORD.get(),
            RecipeCategory.COMBAT,
            ModItems.BREAD_SWORD.get())

        // Bread Shovel
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_SHOVEL.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern(" B ")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_shovel"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_shovel_smithing"),
            ModItems.BREAD_SHOVEL.get(),
            ModItems.RF_BREAD_SHOVEL.get(),
            RecipeCategory.TOOLS,
            ModItems.BREAD_SHOVEL.get())

        // Bread Axe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_AXE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BB ")
            .pattern("BS ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_axe"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_axe_smithing"),
            ModItems.BREAD_AXE.get(),
            ModItems.RF_BREAD_AXE.get(),
            RecipeCategory.TOOLS,
            ModItems.BREAD_AXE.get())

        // Bread Pickaxe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_PICKAXE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BBB")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_pickaxe"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_pickaxe_smithing"),
            ModItems.BREAD_PICKAXE.get(),
            ModItems.RF_BREAD_PICKAXE.get(),
            RecipeCategory.TOOLS,
            ModItems.BREAD_PICKAXE.get())

        // Bread Hoe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_HOE.get(), 1)
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .define('S', Items.STICK)
            .pattern("BB ")
            .pattern(" S ")
            .pattern(" S ")
            .save(pWriter, modLocation("tools", "bread_hoe"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_hoe_smithing"),
            ModItems.BREAD_HOE.get(),
            ModItems.RF_BREAD_HOE.get(),
            RecipeCategory.TOOLS,
            ModItems.BREAD_HOE.get())

        //// Armor
        // Bread Helmet
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_HELMET.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .pattern("BBB")
            .pattern("B B")
            .save(pWriter, modLocation("combat", "bread_helmet"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_helmet_smithing"),
            ModItems.BREAD_HELMET.get(),
            ModItems.RF_BREAD_HELMET.get(),
            RecipeCategory.COMBAT,
            ModItems.BREAD_HELMET.get())

        // Bread Chestplate
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_CHESTPLATE.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .pattern("B B")
            .pattern("BBB")
            .pattern("BBB")
            .save(pWriter, modLocation("combat", "bread_chestplate"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_chestplate_smithing"),
            ModItems.BREAD_CHESTPLATE.get(),
            ModItems.RF_BREAD_CHESTPLATE.get(),
            RecipeCategory.COMBAT,
            ModItems.BREAD_CHESTPLATE.get())

        // Bread Leggings
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_LEGGINGS.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .pattern("BBB")
            .pattern("B B")
            .pattern("B B")
            .save(pWriter, modLocation("combat", "bread_leggings"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_leggings_smithing"),
            ModItems.BREAD_LEGGINGS.get(),
            ModItems.RF_BREAD_LEGGINGS.get(),
            RecipeCategory.COMBAT,
            ModItems.BREAD_LEGGINGS.get())

        // Bread Boots
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_BOOTS.get())
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .define('B', ModBlocks.BREAD_BLOCK.get())
            .pattern("B B")
            .pattern("B B")
            .save(pWriter, modLocation("combat", "bread_boots"))

        modNetheriteSmithing(pWriter,
            modLocation("smithing", "reinforced_bread_boots_smithing"),
            ModItems.BREAD_BOOTS.get(),
            ModItems.RF_BREAD_BOOTS.get(),
            RecipeCategory.COMBAT,
            ModItems.BREAD_BOOTS.get())


        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLOUR_LAYER_BLOCK.get(), 6)
            .unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
            .define('F', ModBlocks.FLOUR_BLOCK.get())
            .pattern("   ")
            .pattern("FFF")
            .pattern("   ")
            .save(pWriter, modLocation("building_blocks", "flour_layer_block"))

        doorBuilder(ModBlocks.BREAD_DOOR.get(), Ingredient.of(ModBlocks.BREAD_BLOCK.get()))
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .save(pWriter, modLocation("redstone", "bread_door"))

        fenceBuilder(ModBlocks.BREAD_FENCE.get(), Ingredient.of(ModBlocks.BREAD_BLOCK.get()))
            .unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
            .save(pWriter, modLocation("decorations", "bread_fence"))

        SpecialRecipeBuilder.special(ModRecipeSerializers.ARMOR_POTION.get())
            .save(pWriter, modLocation("special", "crafting", "armor_potion_doping").toString())
        SpecialRecipeBuilder.special(ModRecipeSerializers.BREAD_SLICE.get())
            .save(pWriter, modLocation("special", "crafting", "bread_slicing").toString())
        SpecialRecipeBuilder.special(ModRecipeSerializers.DOPED_BREAD.get())
            .save(pWriter, modLocation("special", "crafting", "bread_potion_doping").toString())
        SpecialRecipeBuilder.special(ModRecipeSerializers.TOAST_TO_TOAST_SLICE.get())
            .save(pWriter, modLocation("special", "crafting", "toasted_bread_to_toast_slice").toString())

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TOASTER.get().asItem())
            .unlockedBy("has_item", has(ModItems.BREAD_SLICE.get()))
            .define('H', ModItems.TOASTER_HEATING_ELEMENT.get())
            .define('I', Items.IRON_INGOT)
            .define('C', Items.COPPER_INGOT)
            .define('R', Items.REDSTONE)
            .define('B', Items.STONE_BUTTON)
            .pattern("IBI")
            .pattern("CHC")
            .pattern("IRI")
            .save(pWriter, modLocation("misc", "toaster"))
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TOASTER_HEATING_ELEMENT.get())
            .unlockedBy("has_item", has(Items.COPPER_INGOT))
            .define('C', Items.COPPER_INGOT)
            .define('I', Items.IRON_INGOT)
            .pattern("I I")
            .pattern("III")
            .pattern("C C")
            .save(pWriter, modLocation("misc", "toaster_heating_element"))

        FluidEnergyRecipeBuilder(ModItems.DOUGH.get())
            .setTimeRequired(20 * 5)
            .setRFRequired(500)
            .requiresItem(ModItems.FLOUR.get())
            .requiresFluid(FluidTags.WATER, 250)
            .setSerializer(ModRecipeSerializers.DOUGH_MACHINE.get())
            .save(pWriter, modLocation("special", "machine", "flour_to_dough"))
        FluidEnergyRecipeBuilder(ItemStack(ModItems.ULTIMATE_BREAD.get()), FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME))
            .setTimeRequired(20 * 5)
            .setRFRequired(5000)
            .requiresItem(Items.BREAD, 5)
            .setSerializer(ModRecipeSerializers.DOUGH_MACHINE.get())
            .save(pWriter, modLocation("special", "machine", "it_worked"))
        FluidEnergyRecipeBuilder(FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME * 2))
            .setTimeRequired(20 * 5)
            .setRFRequired(10000)
            .requiresItem(Items.OBSIDIAN)
            .setSerializer(ModRecipeSerializers.DOUGH_MACHINE.get())
            .save(pWriter, modLocation("special", "machine", "boo"))
        FluidEnergyRecipeBuilder()
            .setTimeRequired(20 * 5)
            .setRFRequired(-10000)
            .requiresItem(Items.LAVA_BUCKET)
            .setSerializer(ModRecipeSerializers.DOUGH_MACHINE.get())
            .save(pWriter, modLocation("special", "machine", "meow"))
        FluidEnergyRecipeBuilder(ModItems.FLOUR.get())
            .setTimeRequired(20 * 5)
            .setRFRequired(5000)
            .requiresItem(Items.WHEAT)
            .setSerializer(ModRecipeSerializers.WHEAT_CRUSHER.get())
            .save(pWriter, modLocation("special", "machine", "wheat_crushing"))
        FluidEnergyRecipeBuilder(ModItems.TOAST_SLICE.get(), 2)
            .setTimeRequired(20 * 5)
            .requiresItem(ModItems.BREAD_SLICE.get(), 2)
            .setSerializer(ModRecipeSerializers.TOASTER.get())
            .save(pWriter, modLocation("special", "machine", "toaster", "toast"))
        FluidEnergyRecipeBuilder(Items.CHARCOAL, 2)
            .setTimeRequired(20 * 5)
            .requiresItem(ModItems.TOAST_SLICE.get(), 2)
            .setSerializer(ModRecipeSerializers.TOASTER.get())
            .save(pWriter, modLocation("special", "machine", "toaster", "burnt_toast"))
        FluidEnergyRecipeBuilder(ModItems.TOASTED_BREAD.get(), 2)
            .setTimeRequired(20 * 10)
            .requiresItem(Items.BREAD, 2)
            .setSerializer(ModRecipeSerializers.TOASTER.get())
            .save(pWriter, modLocation("special", "machine", "toaster", "toasted_bread"))

        // // Compat
        // Create
        if(ModList.get().isLoaded("create")) {
            CreateMixingRecipeBuilder(ModBlocks.BREAD_BLOCK.get(), 2)
                .heatRequirement(CreateMixingRecipeBuilder.HeatRequirement.HEATED)
                .requiresItem(Items.BREAD, 1)
                .requiresFluid(FluidTags.WATER, FluidType.BUCKET_VOLUME)
                .save(pWriter, modLocation("mixing", "bread_mixing_test"))
        }

        // Mekanism
        if(ModList.get().isLoaded("mekanism")) {
            ItemStackToItemStackRecipeBuilder.crushing(
                IngredientCreatorAccess.item().from(ModBlocks.BREAD_BLOCK.get()),
                ItemStack(MekanismItems.BIO_FUEL.get(), 63)
            ).build { pWriter.accept(it) }
        }

        // ProjectE
        ModItems.PROJECT_E?.also { items ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, items.BREAD_ORB_ITEM.get())
                .unlockedBy("has_item", has(PEItems.MOBIUS_FUEL))
                .define('M', PEItems.MOBIUS_FUEL)
                .define('C', Items.COPPER_INGOT)
                .define('B', ModBlocks.BREAD_BLOCK.get())
                .pattern("CMC")
                .pattern("MBM")
                .pattern("CMC")
                .save(pWriter, modLocation("misc", "bread_orb"))
        }
    }
}