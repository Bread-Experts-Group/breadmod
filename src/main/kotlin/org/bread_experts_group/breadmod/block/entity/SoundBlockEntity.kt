package org.bread_experts_group.breadmod.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.util.container.BaseContainer

class SoundBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlockEntityTypes.SOUND_BLOCK.get(), pos, state), BaseContainer {
    override var items: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)

    override fun setChanged() = super.setChanged()
}