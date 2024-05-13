package breadmod.compat.jei

import breadmod.ModMain
import breadmod.compat.jei.category.DoughMachineRecipeCategory
import breadmod.compat.jei.vanillaExtension.JEIArmorPotionCraftingExtension
import breadmod.compat.jei.vanillaExtension.JEIBreadSliceCraftingExtension
import breadmod.recipe.ArmorPotionRecipe
import breadmod.recipe.BreadSliceRecipe
import breadmod.registry.block.ModBlocks
import breadmod.registry.recipe.ModRecipeTypes
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin @Suppress("unused")
class JEIPlugin : IModPlugin {
    val minecraft: Minecraft = Minecraft.getInstance()

    override fun getPluginUid(): ResourceLocation = ResourceLocation(ModMain.ID, "jei_plugin")

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addCategoryExtension(ArmorPotionRecipe::class.java) { JEIArmorPotionCraftingExtension(it) }
        registration.craftingCategory.addCategoryExtension(BreadSliceRecipe::class.java) { JEIBreadSliceCraftingExtension()}
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val jeiHelpers = registration.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper

        registration.addRecipeCategories(DoughMachineRecipeCategory(guiHelper))
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ModBlocks.DOUGH_MACHINE_BLOCK.get().defaultInstance, ModJEIRecipeTypes.fluidEnergyRecipeType)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipeManager = minecraft.level?.recipeManager ?: throw IllegalStateException()
        val testRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.ENERGY_FLUID_ITEM)

        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
        registration.addRecipes(ModJEIRecipeTypes.fluidEnergyRecipeType, testRecipeList)
    }
}