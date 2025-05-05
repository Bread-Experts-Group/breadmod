package org.bread_experts_group.breadmod.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.IRecipeTransferRegistration
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.screens.WheatCrusherScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.compat.jei.category.ToasterRecipeCategory
import org.bread_experts_group.breadmod.compat.jei.category.WheatCrusherRecipeCategory
import org.bread_experts_group.breadmod.compat.jei.vanilla_extension.JEISliceCraftingExtension
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.ToastSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import java.util.function.Supplier

@JeiPlugin
@Suppress("unused")
class BreadModJeiPlugin : IModPlugin {
	override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin")
	override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
		registration.craftingCategory.addExtension(
			BreadSlicingRecipe::class.java, JEISliceCraftingExtension(
				Items.BREAD, 1, 8, ModItems.BREAD_SLICE.get(), 8, 8
			)
		)
		registration.craftingCategory.addExtension(
			ToastSlicingRecipe::class.java, JEISliceCraftingExtension(
				ModItems.TOASTED_BREAD.get(), 1, 8, ModItems.TOAST_SLICE.get(), 8, 8
			)
		)
	}

	override fun registerCategories(registration: IRecipeCategoryRegistration) {
		val guiHelper = registration.jeiHelpers.guiHelper

		registration.addRecipeCategories(WheatCrusherRecipeCategory(guiHelper))
		registration.addRecipeCategories(ToasterRecipeCategory(guiHelper))
//		registration.addRecipeCategories(DoughMachineRecipeCategory(guiHelper))
	}

	override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
		registration.addRecipeCatalyst(
			ModBlocks.WHEAT_CRUSHER.get().defaultInstance,
			ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
		)
		registration.addRecipeCatalyst(ModBlocks.TOASTER.get().defaultInstance, ModJEIRecipeTypes.TOASTER_RECIPE_TYPE)
		registration.addRecipeCatalyst(
			ModBlocks.DOUGH_MACHINE.get().defaultInstance,
			ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE
		)
	}

	private fun <I : FluidEnergyInput, T : FluidEnergyRecipe<I>> getRecipesForType(
		recipeManager: RecipeManager,
		type: Supplier<RecipeType<T>>
	): List<T> = recipeManager.getAllRecipesFor(type.get()).map { it.value }

	override fun registerRecipes(registration: IRecipeRegistration) {
		val recipeManager = (localClient.level ?: return).recipeManager ?: return
		val wheatCrusherRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.WHEAT_CRUSHING)
		val toasterRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.TOASTING)
		val doughMachineRecipeList = this.getRecipesForType(recipeManager, ModRecipeTypes.DOUGH_MACHINE)

		registration.addRecipes(ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE, wheatCrusherRecipeList)
		registration.addRecipes(ModJEIRecipeTypes.TOASTER_RECIPE_TYPE, toasterRecipeList)
		registration.addRecipes(ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE, doughMachineRecipeList)
	}

	override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
		registration.addRecipeClickArea(
			WheatCrusherScreen::class.java, 84, 34, 7, 48,
			ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
		)
	}

	override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
		registration.addRecipeTransferHandler(
			WheatCrusherMenu::class.java, ModMenuTypes.WHEAT_CRUSHER.get(), ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE,
			0, 2, 2, 36
		)
	}
}