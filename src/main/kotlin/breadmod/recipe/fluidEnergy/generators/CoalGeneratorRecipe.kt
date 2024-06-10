package breadmod.recipe.fluidEnergy.generators

import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType

class CoalGeneratorRecipe(
    pId: ResourceLocation,
    time: Int = 0,
    energy: Int? = null,
    itemsRequired: List<ItemStack>? = null,
    itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
) : FluidEnergyRecipe(pId, time, energy, null, null, itemsRequired, itemsRequiredTagged) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.COAL_GENERATOR
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.COAL_GENERATOR.get()
}