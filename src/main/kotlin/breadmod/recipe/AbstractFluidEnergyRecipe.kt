package breadmod.recipe

import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.PoweredFluidCraftingContainer
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
    final override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean = TODO(" MATCHES")
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
     * @return Tagged fluids required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>
    /**
     * @return Items required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val itemsRequired: List<ItemStack>
    /**
     * @return Tagged items required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>
}
