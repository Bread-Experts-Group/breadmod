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

class FlourToDoughMachineRecipe(
    cId: ResourceLocation,
    override val time: Int,
    override val energy: Int,
    override val fluidsRequired: List<FluidStack>,
    override val fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>,
    override val itemsRequired: List<ItemStack>,
    override val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>
) : AbstractFluidEnergyRecipe(cId) {
    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLOUR_TO_DOUGH.get()
    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = pWidth == 1 && pHeight == 1
}