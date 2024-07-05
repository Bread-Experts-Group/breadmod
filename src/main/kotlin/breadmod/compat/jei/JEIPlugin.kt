package breadmod.compat.jei

import breadmod.ModMain
import breadmod.block.machine.entity.menu.DoughMachineMenu
import breadmod.block.machine.entity.screen.DoughMachineScreen
import breadmod.block.machine.entity.menu.WheatCrusherMenu
import breadmod.block.machine.entity.screen.WheatCrusherScreen
import breadmod.compat.jei.category.DoughMachineRecipeCategory
import breadmod.compat.jei.category.WheatCrusherRecipeCategory
import breadmod.compat.jei.vanillaExtension.JEIArmorPotionCraftingExtension
import breadmod.compat.jei.vanillaExtension.JEIBreadSliceCraftingExtension
import breadmod.recipe.crafting.ArmorPotionRecipe
import breadmod.recipe.crafting.BreadSliceRecipe
import breadmod.registry.block.ModBlocks
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.registry.screen.ModMenuTypes
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.*
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
        registration.addRecipeCategories(WheatCrusherRecipeCategory(guiHelper))
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ModBlocks.DOUGH_MACHINE_BLOCK.get().defaultInstance, ModJEIRecipeTypes.doughMachineRecipeType)
        registration.addRecipeCatalyst(ModBlocks.WHEAT_CRUSHER_BLOCK.get().defaultInstance, ModJEIRecipeTypes.wheatCrusherRecipeType)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipeManager = minecraft.level?.recipeManager ?: throw IllegalStateException()
        val doughMachineRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.DOUGH_MACHINE.get())
        val wheatCrusherRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.WHEAT_CRUSHING.get())

        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
        registration.addRecipes(ModJEIRecipeTypes.doughMachineRecipeType, doughMachineRecipeList)
        registration.addRecipes(ModJEIRecipeTypes.wheatCrusherRecipeType, wheatCrusherRecipeList)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(DoughMachineScreen::class.java, 47, 34, 23, 17, ModJEIRecipeTypes.doughMachineRecipeType)
        registration.addRecipeClickArea(WheatCrusherScreen::class.java, 84, 34, 7, 48, ModJEIRecipeTypes.wheatCrusherRecipeType)
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(
            DoughMachineMenu::class.java, ModMenuTypes.DOUGH_MACHINE.get(), ModJEIRecipeTypes.doughMachineRecipeType,
            0, 3, 3, 36
        )
        registration.addRecipeTransferHandler(
            WheatCrusherMenu::class.java, ModMenuTypes.WHEAT_CRUSHER.get(), ModJEIRecipeTypes.wheatCrusherRecipeType,
            0, 2, 2, 36
        )
    }
}