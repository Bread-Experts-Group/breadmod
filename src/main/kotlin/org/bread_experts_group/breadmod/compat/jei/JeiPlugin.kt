package org.bread_experts_group.breadmod.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.compat.jei.vanilla_extension.JEISliceCraftingExtension
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe

@JeiPlugin
@Suppress("unused")
class JeiPlugin : IModPlugin {
	override fun getPluginUid() : ResourceLocation = modLocation("jei_plugin")
	override fun registerVanillaCategoryExtensions(registration : IVanillaCategoryExtensionRegistration) {
		registration.craftingCategory.addExtension(
			BreadSlicingRecipe::class.java, JEISliceCraftingExtension(
				Items.BREAD, 1, 8, ModItems.BREAD_SLICE.get(), 8, 8
			)
		)
	}
}