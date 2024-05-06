package breadmod.recipe

import breadmod.registry.recipe.ModRecipeSerializers
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

class FlourToDoughMachineRecipe(cId: ResourceLocation): AbstractFluidEnergyRecipe(cId) {
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLOUR_TO_DOUGH.get()
    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = pWidth == 1 && pHeight == 1
}