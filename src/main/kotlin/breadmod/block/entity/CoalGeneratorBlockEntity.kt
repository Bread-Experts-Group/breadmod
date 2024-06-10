package breadmod.block.entity

import breadmod.ModMain.modTranslatable
import breadmod.block.entity.menu.CoalGeneratorMenu
import breadmod.recipe.fluidEnergy.generators.CoalGeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState

class CoalGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractPowerGeneratorBlockEntity<CoalGeneratorRecipe>(
    ModBlockEntities.COAL_GENERATOR.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.COAL_GENERATOR
) {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        CoalGeneratorMenu(pContainerId, pPlayerInventory, this)
    override fun getDisplayName(): Component = modTranslatable("block", "coal_generator")
}