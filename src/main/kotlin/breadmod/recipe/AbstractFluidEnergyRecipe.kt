package breadmod.recipe

import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.PoweredFluidCraftingContainer
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraftforge.fluids.FluidStack

abstract class AbstractFluidEnergyRecipe(pId: ResourceLocation): CustomRecipe(pId, CraftingBookCategory.MISC) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.ENERGY_FLUID_ITEM

    companion object {
        /**
         * Don't stick beans up your nose
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun dsbuyn(): Nothing = throw IllegalStateException("Bad!")
    }

    /**
     * Original assemble function intentionally finally overridden to facilitate returning multiple fluids and items
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    final override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack = dsbuyn()

    /**
     * Original matches function intentionally finally overridden to facilitate checking fluids as well as items for the recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    final override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean = true
    /**
     * Overload of original assemble function to facilitate returning multiple fluids and items
     * @return Items and fluids crafted as a result of this recipe, wrapped in a [Pair] of [List]s
     * @author Miko Elbrecht
     * @since 1.0.0
     */
     fun assemble(pContainer: PoweredFluidCraftingContainer): Pair<List<ItemStack>, List<FluidStack>> = itemsOutput to fluidsOutput

    /**
     * @return If the container is able to craft this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun matches(pContainer: PoweredFluidCraftingContainer, pLevel: Level): Boolean =
        fluidsRequired.all { pContainer.fluidContainer.contains(it, false) != null  }
        && itemsRequired.all { pContainer.itemContainer.items.any { containerItem -> containerItem.`is`(it.item) && containerItem.count >= it.count } }
        && pContainer.energy >= energy

    /**
     * @return Default time this recipe takes to complete
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val time: Int
    /**
     * @return Energy in RF required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val energy: Int
    /**
     * @return Fluids required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val fluidsRequired: List<FluidStack>
    /**
     * @return Items required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val itemsRequired: List<ItemStack>
    /**
     * @return Fluids created as a result of this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val fluidsOutput: List<FluidStack>
    /**
     * @return Items created as a result of this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val itemsOutput: List<ItemStack>
}
