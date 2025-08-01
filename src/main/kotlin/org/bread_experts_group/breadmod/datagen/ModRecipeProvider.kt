package org.bread_experts_group.breadmod.datagen

import moze_intel.projecte.gameObjs.registries.PEItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder
import net.minecraft.data.recipes.SpecialRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.integration.ModIntegrationItems
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadArmorPotionRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.DopedBreadRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.ToastSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyBuilder
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
	output: PackOutput,
	registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(output, registries) {
	override fun buildRecipes(recipeOutput: RecipeOutput) {
		SpecialRecipeBuilder.special { BreadSlicingRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "bread_slicing"))
		SpecialRecipeBuilder.special { ToastSlicingRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "toast_slicing"))
		SpecialRecipeBuilder.special { BreadArmorPotionRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "bread_armor_potion_application"))
		SpecialRecipeBuilder.special { DopedBreadRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "doped_bread_crafting"))
		// Toaster
		this.toasting(Items.BREAD, ModItems.TOASTED_BREAD.get(), recipeOutput, "bread_to_toasted_bread")
		this.toasting(ModItems.BREAD_SLICE.get(), ModItems.TOAST_SLICE.get(), recipeOutput, "slice_to_toast")
		this.toasting(ModItems.TOASTED_BREAD.get(), Items.CHARCOAL, recipeOutput, "toasted_bread_to_charcoal")
		this.toasting(ModItems.TOAST_SLICE.get(), Items.CHARCOAL, recipeOutput, "toast_slice_to_charcoal")
		// Wheat Crushing
		this.wheatCrushing(
			Items.WHEAT to 1,
			ModItems.FLOUR.get() to 2,
			5 * 20,
			2000,
			recipeOutput,
			"wheat_to_flour"
		)
		this.wheatCrushing(
			Items.HAY_BLOCK to 1,
			ModItems.FLOUR.get() to 18,
			15 * 20,
			6000,
			recipeOutput,
			"hay_block_to_flour"
		)
		// Dough Crafting
		this.doughCrafting(
			ModItems.FLOUR.get() to 1,
			Items.GUNPOWDER to 1,
			Fluids.WATER to 100,
			Items.TNT to 1,
			Fluids.LAVA to 500,
			10 * 20,
			5000,
			recipeOutput,
			"dough_machine_test"
		)
		this.doughCrafting(
			ModItems.FLOUR.get() to 1,
			null,
			Fluids.WATER to 250,
			ModItems.DOUGH.get() to 1,
			null,
			5 * 20,
			1000,
			recipeOutput,
			"flour_to_dough"
		)
		this.doughCrafting(
			Items.BREAD to 5,
			null,
			null,
			ModItems.ULTIMATE_BREAD.get() to 1,
			Fluids.WATER to 1000,
			5 * 20,
			5000,
			recipeOutput,
			"ultimate_bread_crafting"
		)
		// FluidEnergyRecipe
		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.BREAD to 16),
			listOf(Fluids.WATER to 500)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.APPLE, 8)
			.fluidRequired(Fluids.LAVA, 500)
			.timeRequired(100)
			.save(recipeOutput, modLocation("fluid_energy", "test_one"))
		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.COOKED_BEEF to 16)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.SPONGE, 8)
			.timeRequired(100)
			.save(recipeOutput, modLocation("fluid_energy", "test_two"))

		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.BREAD to 16, Items.STRING to 16)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.REDSTONE, 8)
			.timeRequired(50)
			.save(recipeOutput, modLocation("fluid_energy", "test_three"))
		// Crafting Table recipes
		nineBlockStorageRecipes(
			recipeOutput,
			RecipeCategory.FOOD,
			Items.BREAD,
			RecipeCategory.BUILDING_BLOCKS,
			ModBlocks.BREAD_BLOCK.asItem(),
			modLocation("building_blocks", "bread_block").toString(),
			null,
			modLocation("food", "bread_block_to_bread").toString(),
			null
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SHIELD.get())
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('I', Items.IRON_INGOT)
			.pattern("BIB")
			.pattern("BBB")
			.pattern(" B ")
			.save(recipeOutput, modLocation("combat", "bread_shield"))
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DOUGH_MACHINE.get())
			.unlockedBy("has_item", has(ModItems.FLOUR.get()))
			.define('I', Items.IRON_INGOT)
			.define('C', Items.COPPER_INGOT)
			.define('R', Items.REDSTONE)
			.define('B', Items.CAULDRON)
			.define('F', Items.FURNACE)
			.pattern("IFI")
			.pattern("CBC")
			.pattern("IRI")
			.save(recipeOutput, modLocation("misc", "dough_machine"))
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BREAD_AMULET.get())
			.unlockedBy("has_item", has(Items.GOLDEN_APPLE))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STRING)
			.define('A', Items.GOLDEN_APPLE)
			.pattern("SBS")
			.pattern("BAB")
			.pattern(" B ")
			.save(recipeOutput, modLocation("misc", "bread_amulet"))

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
			.unlockedBy("has_item", has(Items.CHARCOAL))
			.define('C', Items.CHARCOAL)
			.pattern("CC")
			.pattern("CC")
			.save(recipeOutput, modLocation("building_blocks", "charcoal_low_compaction"))

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get(), 4)
			.unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
			.define('C', ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get())
			.pattern("CCC")
			.pattern("CCC")
			.pattern("CCC")
			.save(recipeOutput, modLocation("building_blocks", "ld_charcoal_compaction_9"))

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
			.unlockedBy("has_item", has(Items.CHARCOAL))
			.define('C', Items.CHARCOAL)
			.pattern("CCC")
			.pattern("CCC")
			.pattern("CCC")
			.save(recipeOutput, modLocation("building_blocks", "charcoal_compaction"))

		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModBlocks.FLOUR_BLOCK.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
			.define('F', ModItems.FLOUR.get())
			.pattern("FF ")
			.pattern("FF ")
			.save(recipeOutput, modLocation("food", "flour_block_from_flour"))

		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 4)
			.unlockedBy("has_item", has(ModItems.FLOUR.get()))
			.requires(ModBlocks.FLOUR_BLOCK.get(), 1)
			.save(recipeOutput, modLocation("food", "flour_from_flour_block"))

		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FLOUR.get(), 1)
			.unlockedBy("has_item", has(Items.WHEAT)) // TODO: Mortar and Pestle for crushing wheat into flour
			.requires(Items.WHEAT, 1)
			.save(recipeOutput, modLocation("food", "flour_from_wheat"))

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
			.unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
			.requires(Items.CHARCOAL, 1)
			.requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 2)
			.save(recipeOutput, modLocation("building_blocks", "ld_charcoal_compaction"))

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 4)
			.unlockedBy("has_item", has(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get()))
			.requires(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get(), 1)
			.save(recipeOutput, modLocation("building_blocks", "ld_charcoal_decompaction"))

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL, 9)
			.unlockedBy("has_item", has(ModBlocks.CHARCOAL_BLOCK.get()))
			.requires(ModBlocks.CHARCOAL_BLOCK.get(), 1)
			.save(recipeOutput, modLocation("building_blocks", "charcoal_decompaction"))

		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.DOUGH.get(), 8)
			.unlockedBy("has_item", has(Items.WHEAT))
			.define('F', ModItems.FLOUR.get())
			.define('B', Items.WATER_BUCKET)
			.pattern("FFF")
			.pattern("FBF")
			.pattern("FFF")
			.save(recipeOutput, modLocation("food", "dough"))

		ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.TEST_BREAD.toStack())
			.unlockedBy("has_item", RecipeProvider.has(Items.BREAD))
			.requires(Items.BREAD, 5)
			.save(recipeOutput, modLocation("special", "test"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_block_smithing"),
			ModBlocks.BREAD_BLOCK.get(),
			ModBlocks.REINFORCED_BREAD_BLOCK.get(),
			RecipeCategory.BUILDING_BLOCKS,
			ModBlocks.BREAD_BLOCK.get()
		)

		SimpleCookingRecipeBuilder.smoking(
			Ingredient.of(ModItems.DOUGH.get()),
			RecipeCategory.FOOD,
			Items.BREAD,
			0f,
			100
		).unlockedBy("has_item", has(ModItems.DOUGH.get()))
			.save(recipeOutput, modLocation("food", "bread_from_smoking"))

		SimpleCookingRecipeBuilder.campfireCooking(
			Ingredient.of(ModItems.DOUGH.get()),
			RecipeCategory.FOOD,
			Items.BREAD,
			0f,
			600
		).unlockedBy("has_item", has(ModItems.DOUGH.get()))
			.save(recipeOutput, modLocation("food", "bread_from_campfire_cooking"))

		SimpleCookingRecipeBuilder.smelting(
			Ingredient.of(ModItems.DOUGH.get()),
			RecipeCategory.FOOD,
			Items.BREAD,
			0f,
			200
		).unlockedBy("has_item", has(ModItems.DOUGH.get()))
			.save(recipeOutput, modLocation("food", "bread_from_smelting"))

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_SWORD.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STICK)
			.pattern(" B ")
			.pattern(" B ")
			.pattern(" S ")
			.save(recipeOutput, modLocation("combat", "bread_sword"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_sword_smithing"),
			ModItems.BREAD_SWORD.get(),
			ModItems.RF_BREAD_SWORD.get(),
			RecipeCategory.COMBAT,
			ModItems.BREAD_SWORD.get()
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_SHOVEL.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STICK)
			.pattern(" B ")
			.pattern(" S ")
			.pattern(" S ")
			.save(recipeOutput, modLocation("tools", "bread_shovel"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_shovel_smithing"),
			ModItems.BREAD_SHOVEL.get(),
			ModItems.RF_BREAD_SHOVEL.get(),
			RecipeCategory.TOOLS,
			ModItems.BREAD_SHOVEL.get()
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_AXE.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STICK)
			.pattern("BB ")
			.pattern("BS ")
			.pattern(" S ")
			.save(recipeOutput, modLocation("tools", "bread_axe"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_axe_smithing"),
			ModItems.BREAD_AXE.get(),
			ModItems.RF_BREAD_AXE.get(),
			RecipeCategory.TOOLS,
			ModItems.BREAD_AXE.get()
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_PICKAXE.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STICK)
			.pattern("BBB")
			.pattern(" S ")
			.pattern(" S ")
			.save(recipeOutput, modLocation("tools", "bread_pickaxe"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_pickaxe_smithing"),
			ModItems.BREAD_PICKAXE.get(),
			ModItems.RF_BREAD_PICKAXE.get(),
			RecipeCategory.TOOLS,
			ModItems.BREAD_PICKAXE.get()
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BREAD_HOE.get(), 1)
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.define('S', Items.STICK)
			.pattern("BB ")
			.pattern(" S ")
			.pattern(" S ")
			.save(recipeOutput, modLocation("tools", "bread_hoe"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_hoe_smithing"),
			ModItems.BREAD_HOE.get(),
			ModItems.RF_BREAD_HOE.get(),
			RecipeCategory.TOOLS,
			ModItems.BREAD_HOE.get()
		)
		// Bread Helmet
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_HELMET.get())
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.pattern("BBB")
			.pattern("B B")
			.save(recipeOutput, modLocation("combat", "bread_helmet"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_helmet_smithing"),
			ModItems.BREAD_HELMET.get(),
			ModItems.RF_BREAD_HELMET.get(),
			RecipeCategory.COMBAT,
			ModItems.BREAD_HELMET.get()
		)
		// Bread Chestplate
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_CHESTPLATE.get())
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.pattern("B B")
			.pattern("BBB")
			.pattern("BBB")
			.save(recipeOutput, modLocation("combat", "bread_chestplate"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_chestplate_smithing"),
			ModItems.BREAD_CHESTPLATE.get(),
			ModItems.RF_BREAD_CHESTPLATE.get(),
			RecipeCategory.COMBAT,
			ModItems.BREAD_CHESTPLATE.get()
		)
		// Bread Leggings
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_LEGGINGS.get())
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.pattern("BBB")
			.pattern("B B")
			.pattern("B B")
			.save(recipeOutput, modLocation("combat", "bread_leggings"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_leggings_smithing"),
			ModItems.BREAD_LEGGINGS.get(),
			ModItems.RF_BREAD_LEGGINGS.get(),
			RecipeCategory.COMBAT,
			ModItems.BREAD_LEGGINGS.get()
		)
		// Bread Boots
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BREAD_BOOTS.get())
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.pattern("B B")
			.pattern("B B")
			.save(recipeOutput, modLocation("combat", "bread_boots"))

		this.modNetheriteSmithing(
			recipeOutput,
			modLocation("smithing", "reinforced_bread_boots_smithing"),
			ModItems.BREAD_BOOTS.get(),
			ModItems.RF_BREAD_BOOTS.get(),
			RecipeCategory.COMBAT,
			ModItems.BREAD_BOOTS.get()
		)

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLOUR_LAYER_BLOCK.get(), 6)
			.unlockedBy("has_item", has(ModBlocks.FLOUR_BLOCK.get()))
			.define('F', ModBlocks.FLOUR_BLOCK.get())
			.pattern("   ")
			.pattern("FFF")
			.pattern("   ")
			.save(recipeOutput, modLocation("building_blocks", "flour_layer_block"))

		doorBuilder(ModBlocks.BREAD_DOOR.get(), Ingredient.of(ModBlocks.BREAD_BLOCK.get()))
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.save(recipeOutput, modLocation("redstone", "bread_door"))

		fenceBuilder(ModBlocks.BREAD_FENCE.get(), Ingredient.of(ModBlocks.BREAD_BLOCK.get()))
			.unlockedBy("has_item", has(ModBlocks.BREAD_BLOCK.get()))
			.save(recipeOutput, modLocation("decorations", "bread_fence"))
//		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TOASTER.get().asItem())
//			.unlockedBy("has_item", has(ModItems.BREAD_SLICE.get()))
//			.define('H', ModItems.TOASTER_HEATING_ELEMENT.get())
//			.define('I', Items.IRON_INGOT)
//			.define('C', Items.COPPER_INGOT)
//			.define('R', Items.REDSTONE)
//			.define('B', Items.STONE_BUTTON)
//			.pattern("IBI")
//			.pattern("CHC")
//			.pattern("IRI")
//			.save(recipeOutput, modLocation("misc", "toaster"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TOASTER_HEATING_ELEMENT.get())
			.unlockedBy("has_item", has(Items.COPPER_INGOT))
			.define('C', Items.COPPER_INGOT)
			.define('I', Items.IRON_INGOT)
			.pattern("I I")
			.pattern("III")
			.pattern("C C")
			.save(recipeOutput, modLocation("misc", "toaster_heating_element"))

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WRENCH.get())
			.unlockedBy("has_item", has(Items.IRON_INGOT))
			.define('I', Items.IRON_INGOT)
			.define('O', Items.ORANGE_DYE)
			.define('B', Items.BLACK_DYE)
			.pattern("  I")
			.pattern("BIO")
			.pattern("I  ")
			.save(recipeOutput, modLocation("tools", "wrench"))

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WRENCH.get())
			.unlockedBy("has_item", has(Items.IRON_INGOT))
			.define('I', Items.IRON_INGOT)
			.define('O', Items.ORANGE_DYE)
			.define('B', Items.BLACK_DYE)
			.pattern("I  ")
			.pattern("OIB")
			.pattern("  I")
			.save(recipeOutput, modLocation("tools", "wrench_alt"))
		// Special (Needs) Character recipes
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RICARD_BLOCK.get())
			.unlockedBy("has_item", has(Items.ROSE_BUSH))
			.define('R', Items.ROSE_BUSH)
			.define('S', Items.SUGAR)
			.define('B', Items.BROWN_WOOL)
			.define('W', Items.BLACK_WOOL)
			.define('P', Items.BLAZE_POWDER)
			.pattern("SSS")
			.pattern("BPR")
			.pattern("BWW")
			.save(recipeOutput, modLocation("decorations", "characters", "ricard_block"))

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.UNFUNNYLAD_BLOCK.get())
			.unlockedBy("has_item", has(Items.POTATO))
			.define('P', Items.POTATO)
			.define('W', Items.WHITE_WOOL)
			.define('S', Items.STICK)
			.define('B', Items.BROWN_WOOL)
			.pattern("WPW")
			.pattern("SBS")
			.pattern(" B ")
			.save(recipeOutput, modLocation("decorations", "characters", "unfunnylad_block"))

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.OMANEKO_BLOCK.get())
			.unlockedBy("has_item", has(Items.GREEN_WOOL))
			.define('G', Items.GREEN_WOOL)
			.define('B', Items.LIGHT_BLUE_WOOL)
			.define('b', Items.BLACK_CONCRETE)
			.define('W', Items.WHITE_WOOL)
			.define('R', Items.RED_DYE)
			.define('Y', Items.YELLOW_DYE)
			.pattern("YWR")
			.pattern("BGB")
			.pattern("bBb")
			.save(recipeOutput, modLocation("decorations", "characters", "omaneko_block"))

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.NIKO_BLOCK.get())
			.unlockedBy("has_item", has(Items.GLOWSTONE))
			.define('G', Items.GLOWSTONE)
			.define('H', Items.HONEY_BOTTLE)
			.define('S', Items.SUGAR)
			.define('W', Items.WHEAT)
			.define('R', Items.RED_WOOL)
			.define('B', Items.BLUE_WOOL)
			.pattern("HSR")
			.pattern("RWG")
			.pattern("BRB")
			.save(recipeOutput, modLocation("decorations", "characters", "niko_block"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModIntegrationItems.ProjectEItems.BREAD_ORB.get())
			.unlockedBy("has_item", has(PEItems.MOBIUS_FUEL))
			.define('M', PEItems.MOBIUS_FUEL)
			.define('C', Items.COPPER_INGOT)
			.define('B', ModBlocks.BREAD_BLOCK.get())
			.pattern("CMC")
			.pattern("MBM")
			.pattern("CMC")
			.save(recipeOutput, modLocation("misc", "bread_orb"))
	}

	private fun toasting(input: Item, result: Item, output: RecipeOutput, name: String): Unit =
		FluidEnergyBuilder(::ToasterRecipe, result to 2)
			.itemRequired(input, 2)
			.timeRequiredInSeconds(5)
			.save(output, modLocation("machine", "toasting", name))

	private fun wheatCrushing(
		input: Pair<Item, Int>,
		result: Pair<Item, Int>,
		ticks: Int,
		energy: Int,
		output: RecipeOutput,
		name: String
	): Unit = FluidEnergyBuilder(::WheatCrusherRecipe, listOf(result))
		.itemRequired(input)
		.timeRequired(ticks)
		.energyRequired(energy)
		.save(output, modLocation("machine", "wheat_crushing", name))

	private fun doughCrafting(
		inputOne: Pair<Item, Int>,
		inputTwo: Pair<Item, Int>?,
		fluidInput: Pair<Fluid, Int>?,
		itemOutput: Pair<Item, Int>?,
		fluidOutput: Pair<Fluid, Int>?,
		ticks: Int,
		energy: Int,
		output: RecipeOutput,
		name: String
	): Unit = FluidEnergyBuilder(::DoughMachineRecipe, itemOutput, fluidOutput)
		.itemRequired(inputOne)
		.itemRequired(inputTwo)
		.fluidRequired(fluidInput)
		.timeRequired(ticks)
		.energyRequired(energy)
		.save(output, modLocation("machine", "dough_crafting", name))

	private fun modNetheriteSmithing(
		recipeOutput: RecipeOutput,
		recipeLocation: ResourceLocation,
		ingredient: Item,
		result: Item,
		category: RecipeCategory,
		unlockItem: Item
	) {
		SmithingTransformRecipeBuilder.smithing(
			Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
			Ingredient.of(ingredient),
			Ingredient.of(Items.NETHERITE_INGOT),
			category,
			result
		).unlocks("has_item", has(unlockItem))
			.save(recipeOutput, recipeLocation)
	}
}