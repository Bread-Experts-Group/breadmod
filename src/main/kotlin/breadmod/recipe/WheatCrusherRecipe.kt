package breadmod.recipe

import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
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
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import kotlin.jvm.optionals.getOrNull

open class WheatCrusherRecipe(
    pId: ResourceLocation,
    open val time: Int = 0,
    open val energy: Int? = null,
    open val itemsRequired: List<ItemStack>? = null,
    open val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null,
    open val itemsOutput: List<ItemStack>? = null
) : CustomRecipe(pId, CraftingBookCategory.MISC) {
    override fun getType(): RecipeType<*> = ModRecipeTypes.WHEAT_CRUSHING

    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        val valid = itemsRequired?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.item) }?.count ?: -1) >= it.count } ?: true &&
                    itemsRequiredTagged?.all { (pContainer.items.firstOrNull { conItem -> conItem.`is`(it.first) }?.count ?: -1) >= it.second } ?: true
        if(valid) {
            val entityCheck = pContainer as? ICapabilityProvider
            if(energy != null && energy!! > 0) {
                val energyHandle = entityCheck?.getCapability(ForgeCapabilities.ENERGY)?.resolve()?.getOrNull() ?: return false
                if(energyHandle.energyStored < energy!!) return false
            }
        }
        return valid
    }

    open fun canFitResults(itemOutput: Pair<List<ItemStack>, List<Int>>?): Boolean {
        itemsOutput?.also {
            if(itemOutput == null) return false
            else {
                val check = itemOutput.first.filterIndexed { index, _ -> itemOutput.second.contains(index) }
                it.forEach { stack -> if(!check.any { slot -> slot.isEmpty || (slot.`is`(stack.item) && slot.count < stack.maxStackSize) }) return false }
            }
        }
        return true
    }

    open fun assembleOutputs(pContainer: CraftingContainer, pLevel: Level): List<ItemStack> = itemsOutput ?: listOf()

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack = ItemStack.EMPTY

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = true
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.WHEAT_CRUSHING.get()
}