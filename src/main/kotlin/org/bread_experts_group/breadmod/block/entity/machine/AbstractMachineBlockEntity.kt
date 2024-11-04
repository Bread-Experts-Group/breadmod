package org.bread_experts_group.breadmod.block.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.ContainerHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.util.container.BaseCraftingContainer
import org.bread_experts_group.breadmod.util.container.WorldlyBaseCraftingContainer
import java.util.Optional

abstract class AbstractMachineBlockEntity<I : RecipeInput, T : Recipe<I>, R : BlockEntity>(
    type: BlockEntityType<R>,
    pos: BlockPos,
    state: BlockState,
    private val craftingContainerSize: Int,
    private val recipeType: RecipeType<T>
) : BlockEntity(type, pos, state), BaseCraftingContainer {
    override var items: NonNullList<ItemStack> = NonNullList.withSize(craftingContainerSize, ItemStack.EMPTY)

    var currentRecipe: Optional<RecipeHolder<T>> = Optional.empty()
    val recipeDial: RecipeManager.CachedCheck<I, T> by lazy { RecipeManager.createCheck(recipeType) }

    abstract fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: R)

    open fun recipeDone(level: Level, recipe: T) {}
    open fun consumeRecipe(level: Level, recipe: T) {}

    open fun adjustSaveAdditional(tag: CompoundTag, registries: Provider) {}
    final override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        ContainerHelper.saveAllItems(tag, items, registries)
        adjustSaveAdditional(tag, registries)
    }

    open fun adjustLoadAdditional(tag: CompoundTag, registries: Provider) {}
    final override fun loadAdditional(tag: CompoundTag, registries: Provider) {
        super.loadAdditional(tag, registries)

        items = NonNullList.withSize(craftingContainerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, items, registries)
        adjustLoadAdditional(tag, registries)
    }
}