package breadmod.block.entity

import breadmod.BreadMod
import breadmod.block.BreadFurnaceBlock
import breadmod.block.entity.menu.BreadFurnaceMenu
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.state.BlockState

class BreadFurnaceBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractFurnaceBlockEntity(ModBlockEntities.BREAD_FURNACE.get(), pPos, pBlockState, ModRecipeTypes.BREAD_REFINEMENT) {
    override fun createMenu(pContainerId: Int, pInventory: Inventory): AbstractContainerMenu =
        BreadFurnaceMenu(pContainerId, pInventory)
    override fun getDefaultName(): Component = BreadMod.modTranslatable("container", "bread_furnace")
}