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

open class FluidEnergyRecipe(pId: ResourceLocation): CustomRecipe(pId, CraftingBookCategory.MISC) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.ENERGY_FLUID_ITEM

    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        val okay =
                itemsRequired?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.item) }?.count ?: -1) >= it.count } ?: true &&
                itemsRequiredTagged?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.first) }?.count ?: -1) >= it.second } ?: true
        if(okay && (fluidsRequired != null || fluidsRequiredTagged != null)) {
            val fluidHandler = (pContainer as? BlockEntity)?.getCapability(ForgeCapabilities.FLUID_HANDLER)?.resolve()?.getOrNull() ?: return false

            return fluidsRequired?.all { fluidHandler.amount(it.fluid) >= it.amount } ?: true &&
                    fluidsRequiredTagged?.all { fluidHandler.amount(it.first) >= it.second } ?: true
        } else return false
    }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = true
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY.get()

    /**
     * @return Default time this recipe takes to complete
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val time: Int = 0
    /**
     * @return Energy in RF required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val energy: Int? = null
    /**
     * @return Fluids required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val fluidsRequired: List<FluidStack>? = null
    /**
     * @return Tagged fluids required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>? = null
    /**
     * @return Items required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val itemsRequired: List<ItemStack>? = null
    /**
     * @return Tagged items required to complete this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null
    /**
     * @return Items created as a result of this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val itemsOutput: List<ItemStack>? = null
    /**
     * @return Fluids created as a result of this recipe
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    open val fluidsOutput: List<FluidStack>? = null
}
