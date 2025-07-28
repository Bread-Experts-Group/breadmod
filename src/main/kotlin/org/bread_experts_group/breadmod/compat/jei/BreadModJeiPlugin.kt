package org.bread_experts_group.breadmod.compat.jei
//@JeiPlugin
//@Suppress("unused")
//class BreadModJeiPlugin : IModPlugin {
//	override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin")
//	override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
//		val category = registration.craftingCategory
//		category.addExtension(
//			BreadSlicingRecipe::class.java, JEISliceCraftingExtension(
//				Items.BREAD, 1, 8, ModItems.BREAD_SLICE.get(), 8, 8
//			)
//		)
//		category.addExtension(
//			ToastSlicingRecipe::class.java, JEISliceCraftingExtension(
//				ModItems.TOASTED_BREAD.get(), 1, 8, ModItems.TOAST_SLICE.get(), 8, 8
//			)
//		)
//		// todo bread and doped bread potion crafting extensions
//	}
//
//	override fun registerCategories(registration: IRecipeCategoryRegistration) {
//		val guiHelper = registration.jeiHelpers.guiHelper
//
//		registration.addRecipeCategories(ToasterRecipeCategory(guiHelper))
//		registration.addRecipeCategories(DoughMachineRecipeCategory(guiHelper))
//		registration.addRecipeCategories(WheatCrusherRecipeCategory(guiHelper))
//	}
//
//	override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
//		registration.addRecipeCatalyst(
//			ModBlocks.WHEAT_CRUSHER.get().defaultInstance,
//			ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
//		)
//		registration.addRecipeCatalyst(ModBlocks.TOASTER.get().defaultInstance, ModJEIRecipeTypes.TOASTER_RECIPE_TYPE)
//		registration.addRecipeCatalyst(
//			ModBlocks.DOUGH_MACHINE.get().defaultInstance,
//			ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE
//		)
//	}
//
//	private fun <T : FluidEnergyRecipe> getRecipesForType(
//		recipeManager: RecipeManager,
//		type: Supplier<RecipeType<T>>
//	): List<T> = recipeManager.getAllRecipesFor(type.get()).map { it.value }
//
//	override fun registerRecipes(registration: IRecipeRegistration) {
//		val recipeManager = (localClient.level ?: return).recipeManager ?: return
//		val wheatCrusherRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.WHEAT_CRUSHING)
//		val toasterRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.TOASTING)
//		val doughMachineRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.DOUGH_MACHINE)
//
//		registration.addRecipes(ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE, wheatCrusherRecipeList)
//		registration.addRecipes(ModJEIRecipeTypes.TOASTER_RECIPE_TYPE, toasterRecipeList)
//		registration.addRecipes(ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE, doughMachineRecipeList)
//	}
//
//	override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
//		registration.addRecipeClickArea(
//			WheatCrusherScreen::class.java, 84, 34, 7, 48,
//			ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
//		)
//		registration.addRecipeClickArea(
//			DoughMachineScreen::class.java, 29, 35, 13, 13,
//			ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE
//		)
//	}
//
//	// todo fix transfer handlers throwing
//	override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
//		registration.addRecipeTransferHandler(
//			WheatCrusherMenu::class.java, ModMenuTypes.WHEAT_CRUSHER.get(), ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE,
//			0, 2, 2, 35
//		)
//	}
//}