package bread.mod.breadmod.block.entity

import bread.mod.breadmod.recipe.toaster.ToasterInput
import bread.mod.breadmod.recipe.toaster.ToasterRecipe
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import java.util.Optional

class ToasterBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BlockEntity(ModBlockEntityTypes.TOASTER.get(), pos, state), CraftingContainer {
    var currentRecipe: Optional<RecipeHolder<ToasterRecipe>> = Optional.empty()
    var items: NonNullList<ItemStack> = NonNullList.withSize(1, ItemStack.EMPTY)

    val recipeDial: RecipeManager.CachedCheck<ToasterInput, ToasterRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.TOASTING.get())
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: ToasterBlockEntity) {
        val logger = LogManager.getLogger()
        currentRecipe.ifPresentOrElse({
            logger.info("a recipe is currently selected: ${currentRecipe.get()}")
        }, {
            val recipe = recipeDial.getRecipeFor(
                ToasterInput(getItem(0), getItem(0).count), level
            )
            recipe.ifPresentOrElse({
                logger.info("we have a recipe: ${recipe.get()}")
                currentRecipe = recipe
            }, {
                logger.info("we don't have a recipe")
            })
        })
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, items, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        items = NonNullList.withSize(containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, items, registries)
    }

    override fun getContainerSize(): Int = 1
    override fun isEmpty(): Boolean = items.count() == 0
    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack =
        items[slot].split(amount) ?: ItemStack.EMPTY

    override fun removeItemNoUpdate(slot: Int): ItemStack =
        items[slot].copyAndClear() ?: ItemStack.EMPTY

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        stack.limitSize(2)
        setChanged()
    }

    override fun setChanged() = super.setChanged()

    override fun stillValid(player: Player): Boolean =
        Container.stillValidBlockEntity(this, player)

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): List<ItemStack> = items
    override fun clearContent() = items.clear()

    override fun fillStackedContents(contents: StackedContents) {
        for (stack: ItemStack in items) {
            contents.accountSimpleStack(stack)
        }
    }
}