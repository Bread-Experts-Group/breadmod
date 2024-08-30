package breadmod.recipe.fluidEnergy

import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType

class WheatCrushingRecipe(
    pId: ResourceLocation,
    time: Int = 0,
    energy: Int? = null,
    itemsRequired: List<ItemStack>? = null,
    itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
    itemsOutput: List<ItemStack>? = null,
) : FluidEnergyRecipe(
    pId,
    time,
    energy = energy,
    itemsRequired = itemsRequired,
    itemsRequiredTagged = itemsRequiredTagged,
    itemsOutput = itemsOutput
) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.WHEAT_CRUSHING.get()
    override fun getSerializer(): SimpleFluidEnergyRecipeSerializer<*> = ModRecipeSerializers.WHEAT_CRUSHER.get()
}