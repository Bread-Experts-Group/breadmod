package breadmod.recipe

import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.amount
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import kotlin.jvm.optionals.getOrNull

abstract class AbstractFluidEnergyRecipe(pId: ResourceLocation): CustomRecipe(pId, CraftingBookCategory.MISC) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.ENERGY_FLUID_ITEM

    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        val okay =
                itemsRequired.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.item) }?.count ?: -1) >= it.count } &&
                itemsRequiredTagged.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.first) }?.count ?: -1) >= it.second }
        if(okay && (fluidsRequiredTagged.size + fluidsRequired.size) > 0) {
            val fluidHandler = (pContainer as? BlockEntity)?.getCapability(ForgeCapabilities.FLUID_HANDLER)?.resolve()?.getOrNull() ?: return false

            return fluidsRequired.all { fluidHandler.amount(it.fluid) >= it.amount } &&
                    fluidsRequiredTagged.all { fluidHandler.amount(it.first) >= it.second }
        } else return false
    }
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
