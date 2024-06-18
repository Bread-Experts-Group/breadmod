package breadmodadvanced.recipe.fluidEnergy.generators

import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import breadmodadvanced.registry.recipe.ModRecipeSerializersAdv
import breadmodadvanced.registry.recipe.ModRecipeTypesAdv
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

class DieselGeneratorRecipe(
    pId: ResourceLocation,
    time: Int = 0,
    energy: Int? = null,
    fluidsRequired: List<FluidStack>? = null,
    fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>? = null,
    itemsRequired: List<ItemStack>? = null,
    itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
) : FluidEnergyRecipe(pId, time, energy, fluidsRequired, fluidsRequiredTagged, itemsRequired, itemsRequiredTagged) {
    override fun getType(): RecipeType<*> = ModRecipeTypesAdv.DIESEL_GENERATOR.get()
    override fun getSerializer(): SimpleFluidEnergyRecipeSerializer<*> = ModRecipeSerializersAdv.DIESEL_GENERATOR.get()
}