package breadmod.recipe.fluidEnergy

import breadmod.block.machine.CraftingManager
import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

abstract class FluidEnergyRecipe(
    pId: ResourceLocation,
    open val time: Int = 0,
    open val energy: Int? = null,
    open val fluidsRequired: List<FluidStack>? = null,
    open val fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>? = null,
    open val itemsRequired: List<ItemStack>? = null,
    open val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
    open val fluidsOutput: List<FluidStack>? = null,
    open val itemsOutput: List<ItemStack>? = null,
) : CustomRecipe(pId, CraftingBookCategory.MISC) {
    abstract override fun getType(): RecipeType<*>
    abstract override fun getSerializer(): SimpleFluidEnergyRecipeSerializer<*>

    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        val okay = itemsRequired?.all { r ->
            pContainer.items.firstOrNull { it.`is`(r.item) }?.let { it.count >= r.count } ?: false
        } ?: true
                && itemsRequiredTagged?.all { r ->
            pContainer.items.firstOrNull { it.`is`(r.first) }?.let { it.count >= r.second } ?: false
        } ?: true

        if (okay) {
            val entityCheck = (pContainer as CraftingManager<*>).parent
            if (energy != null) {
                val energyHandle =
                    entityCheck.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)
                        ?: return false
                if (energyHandle.energyStored < energy!!) return false
            }

            if (!fluidsRequired.isNullOrEmpty() || !fluidsRequiredTagged.isNullOrEmpty()) {
                val fluidHandle =
                    entityCheck.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)
                        ?: return false
                return (fluidsRequired?.all { fluidHandle.amount(it.fluid) >= it.amount } ?: true) &&
                        (fluidsRequiredTagged?.all { fluidHandle.amount(it.first) >= it.second } ?: true)
            }
        }
        return okay
    }

    open fun canFitResults(itemOutput: Pair<List<ItemStack>, List<Int>>?, fluidOutput: IFluidHandler?): Boolean {
        if (!fluidsOutput.isNullOrEmpty() && fluidOutput == null) return false
        else fluidsOutput?.forEach {
            if ((fluidOutput?.fill(it, IFluidHandler.FluidAction.SIMULATE) ?: -1) < it.amount) return false
        }
        itemsOutput?.also {
            if (itemOutput == null) return false
            else {
                val check = itemOutput.first.filterIndexed { index, _ -> itemOutput.second.contains(index) }
                it.forEach { stack -> if (!check.any { slot -> slot.isEmpty || (slot.`is`(stack.item) && slot.count < stack.maxStackSize) }) return false }
            }
        }
        return true
    }

    open fun assembleOutputs(pContainer: CraftingContainer, pLevel: Level): Pair<List<ItemStack>, List<FluidStack>> =
        (itemsOutput ?: listOf()) to (fluidsOutput ?: listOf())

    final override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack =
        ItemStack.EMPTY


    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = true
}
