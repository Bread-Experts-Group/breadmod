package breadmod.client.jei

import breadmod.ModMain
import breadmod.client.jei.category.DoughMachineRecipeCategory
import breadmod.client.jei.category.ToasterRecipeCategory
import breadmod.client.jei.category.WheatCrusherRecipeCategory
import breadmod.client.jei.vanillaExtension.JEIArmorPotionCraftingExtension
import breadmod.client.jei.vanillaExtension.JEISliceCraftingExtension
import breadmod.client.screen.DoughMachineScreen
import breadmod.client.screen.WheatCrusherScreen
import breadmod.menu.block.DoughMachineMenu
import breadmod.menu.block.WheatCrusherMenu
import breadmod.recipe.crafting.ArmorPotionRecipe
import breadmod.recipe.crafting.BreadSliceRecipe
import breadmod.recipe.crafting.ToastedBreadSliceRecipe
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.render.rgMinecraft
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items

@JeiPlugin
@Suppress("unused")
class JEIPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation = ResourceLocation(ModMain.ID, "jei_plugin")

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addCategoryExtension(ArmorPotionRecipe::class.java) {
            JEIArmorPotionCraftingExtension(
                it
            )
        }
        registration.craftingCategory.addCategoryExtension(BreadSliceRecipe::class.java) {
            JEISliceCraftingExtension(
                Items.BREAD, ModItems.BREAD_SLICE.get(), 1, 8, 8, 8
            )
        }
        registration.craftingCategory.addCategoryExtension(ToastedBreadSliceRecipe::class.java) {
            JEISliceCraftingExtension(
                ModItems.TOASTED_BREAD.get(), ModItems.TOAST_SLICE.get(), 1, 8, 8, 8
            )
        }
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val jeiHelpers = registration.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper

        registration.addRecipeCategories(DoughMachineRecipeCategory(guiHelper))
        registration.addRecipeCategories(WheatCrusherRecipeCategory(guiHelper))
        registration.addRecipeCategories(ToasterRecipeCategory(guiHelper))
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(
            ModBlocks.DOUGH_MACHINE_BLOCK.get().defaultInstance,
            ModJEIRecipeTypes.doughMachineRecipeType
        )
        registration.addRecipeCatalyst(
            ModBlocks.WHEAT_CRUSHER_BLOCK.get().defaultInstance,
            ModJEIRecipeTypes.wheatCrusherRecipeType
        )
        registration.addRecipeCatalyst(ModBlocks.TOASTER.get().defaultInstance, ModJEIRecipeTypes.toasterRecipeType)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipeManager = rgMinecraft.level?.recipeManager ?: throw IllegalStateException()
        val doughMachineRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.DOUGH_MACHINE.get())
        val wheatCrusherRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.WHEAT_CRUSHING.get())
        val toasterRecipeList = recipeManager.getAllRecipesFor(ModRecipeTypes.TOASTING.get())

        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
        registration.addItemStackInfo(
            ModBlocks.TOASTER.get().defaultInstance,
            Component.literal("DON'T LISTEN TO THE TOOLTIP COOK CHARCOAL IN IT!!")
        )
        registration.addRecipes(ModJEIRecipeTypes.doughMachineRecipeType, doughMachineRecipeList)
        registration.addRecipes(ModJEIRecipeTypes.wheatCrusherRecipeType, wheatCrusherRecipeList)
        registration.addRecipes(ModJEIRecipeTypes.toasterRecipeType, toasterRecipeList)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(
            DoughMachineScreen::class.java, 47, 34, 23, 17,
            ModJEIRecipeTypes.doughMachineRecipeType
        )
        registration.addRecipeClickArea(
            WheatCrusherScreen::class.java, 84, 34, 7, 48,
            ModJEIRecipeTypes.wheatCrusherRecipeType
        )
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