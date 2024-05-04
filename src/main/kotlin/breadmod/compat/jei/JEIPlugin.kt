package breadmod.compat.jei

import breadmod.BreadMod
import breadmod.compat.jei.vanillaExtensions.JEIArmorPotionCraftingExtension
import breadmod.recipe.ArmorPotionRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class JEIPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation = ResourceLocation(BreadMod.ID, "jei_plugin")

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addCategoryExtension(ArmorPotionRecipe::class.java) { JEIArmorPotionCraftingExtension(it) }
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
    }
}