package breadmod.compat.jei

import breadmod.ModMain
import breadmod.compat.jei.vanillaExtensions.JEIArmorPotionCraftingExtension
import breadmod.compat.jei.vanillaExtensions.JEIBreadSliceCraftingExtension
import breadmod.recipe.ArmorPotionRecipe
import breadmod.recipe.BreadSliceRecipe
import breadmod.registry.block.ModBlocks
import breadmod.registry.recipe.ModRecipeTypes
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin @Suppress("unused")
class JEIPlugin : IModPlugin {
    val minecraft: Minecraft = Minecraft.getInstance()
    val world = minecraft.level
    private val recipeManager = world?.recipeManager

    override fun getPluginUid(): ResourceLocation = ResourceLocation(ModMain.ID, "jei_plugin")

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addCategoryExtension(ArmorPotionRecipe::class.java) { JEIArmorPotionCraftingExtension(it) }
        registration.craftingCategory.addCategoryExtension(BreadSliceRecipe::class.java) { JEIBreadSliceCraftingExtension()}
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val jeiHelpers = registration.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper

        registration.addRecipeCategories(TestRecipeCategory(guiHelper))
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        // TODO somehow we need to get a workable list of recipes that this recipe type provides, then pass it to addRecipes so jei can populate the category
        val testRecipeList = this.recipeManager?.getAllRecipesFor(ModRecipeTypes.ENERGY_FLUID_ITEM)?.stream()
        println("test recipe list print")
        println(testRecipeList) // returns null..


        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
//        registration.addRecipes(ModJEIRecipeTypes.fluidEnergyRecipeType, testRecipeList)
    }
}