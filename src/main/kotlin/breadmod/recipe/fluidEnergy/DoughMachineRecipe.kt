package breadmod.recipe.fluidEnergy

import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

class DoughMachineRecipe(
    pId: ResourceLocation,
    time: Int = 0,
    energy: Int? = null,
    fluidsRequired: List<FluidStack>? = null,
    fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>? = null,
    itemsRequired: List<ItemStack>? = null,
    itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
    fluidsOutput: List<FluidStack>? = null,
    itemsOutput: List<ItemStack>? = null,
): FluidEnergyRecipe(pId, time, energy, fluidsRequired, fluidsRequiredTagged, itemsRequired, itemsRequiredTagged, fluidsOutput, itemsOutput) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.DOUGH_MACHINE
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.DOUGH_MACHINE.get()
}