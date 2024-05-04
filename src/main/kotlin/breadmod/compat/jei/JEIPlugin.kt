package breadmod.compat.jei

import breadmod.BreadMod
import breadmod.recipe.ArmorPotionRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation


@JeiPlugin
class JEIPlugin : IModPlugin {
    //    override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin") // debug
    private val armorPotionRecipeType: RecipeType<ArmorPotionRecipe> = RecipeType.create(BreadMod.ID, "bread_potion_crafting", ArmorPotionRecipe::class.java)

    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(BreadMod.ID, "jei_plugin")
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
//        registration.addRecipes(armorPotionRecipeType, ) // @VanillaPlugin.class
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val jeiHelpers = registration.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper
        BreadMod.LOGGER.info("REGISTERING BREAD MOD RECIPE CATEGORY")
        registration.addRecipeCategories(
            BreadModCategory(guiHelper)
        )
    }

    /*
    [16:54:34] [Render thread/ERROR] [me.je.li.lo.PluginCaller/]: Caught an error from mod plugin: class breadmod.compat.jei.JEIPlugin breadmod:jei_plugin
    java.lang.NullPointerException: recipeCategories must not contain null values.
	at mezz.jei.common.util.ErrorUtil.checkNotEmpty(ErrorUtil.java:94) ~[jei-1.20.1-forge-15.3.0.4_mapped_parchment_2023.09.03-1.20.1.jar:15.3.0.4] {re:classloading}
	at mezz.jei.library.load.registration.RecipeCategoryRegistration.addRecipeCategories(RecipeCategoryRegistration.java:29) ~[jei-1.20.1-forge-15.3.0.4_mapped_parchment_2023.09.03-1.20.1.jar:15.3.0.4] {re:classloading}
	at breadmod.compat.jei.JEIPlugin.registerCategories(JEIPlugin.kt:29) ~[main/:?] {re:classloading}
     */
}