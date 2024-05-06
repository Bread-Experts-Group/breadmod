package breadmod.recipe

import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.amount
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import kotlin.jvm.optionals.getOrNull

open class FluidEnergyRecipe(
    pId: ResourceLocation,
    open val time: Int = 0,
    open val energy: Int? = null,
    open val fluidsRequired: List<FluidStack>? = null,
    open val fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>? = null,
    open val itemsRequired: List<ItemStack>? = null,
    open val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
    open val fluidsOutput: List<FluidStack>? = null,
    open val itemsOutput: List<ItemStack>? = null,
): CustomRecipe(pId, CraftingBookCategory.MISC) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.ENERGY_FLUID_ITEM

    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        val okay =
                itemsRequired?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.item) }?.count ?: -1) >= it.count } ?: true &&
                itemsRequiredTagged?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.first) }?.count ?: -1) >= it.second } ?: true
        if(okay) {
            val entityCheck = pContainer as? BlockEntity
            if(energy != null && energy!! > 0) {
                val energyHandle = entityCheck?.getCapability(ForgeCapabilities.ENERGY)?.resolve()?.getOrNull() ?: return false
                if(energyHandle.energyStored < energy!!) return false
            }

            if(!fluidsRequired.isNullOrEmpty() || !fluidsRequiredTagged.isNullOrEmpty()) {
                val fluidHandle = entityCheck?.getCapability(ForgeCapabilities.FLUID_HANDLER)?.resolve()?.getOrNull() ?: return false
                return (fluidsRequired?.all { fluidHandle.amount(it.fluid) >= it.amount } ?: true) &&
                    (fluidsRequiredTagged?.all { fluidHandle.amount(it.first) >= it.second } ?: true)
            }
        }
        return okay
    }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = true
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY.get()
}
