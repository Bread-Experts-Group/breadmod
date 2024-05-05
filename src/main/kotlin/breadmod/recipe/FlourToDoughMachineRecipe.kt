package breadmod.recipe

import breadmod.registry.recipe.ModRecipeSerializers
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraftforge.fluids.FluidStack

class FlourToDoughMachineRecipe(
    cId: ResourceLocation,
    override val energy: Int,
    override val time: Int,
    override val fluidsRequired: List<FluidStack>,
    override val itemsRequired: List<ItemStack>,
    override val fluidsOutput: List<FluidStack>,
    override val itemsOutput: List<ItemStack>
) : AbstractFluidEnergyRecipe(cId) {
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLOUR_TO_DOUGH.get()
    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = pWidth == 1 && pHeight == 1
}