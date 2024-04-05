package breadmod.block

import breadmod.block.registry.ModBlockEntities.BREAD_FURNACE_BLOCK_ENTITY_TYPE
import breadmod.recipe.ModRecipeTypes
import breadmod.screens.ModMenuTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.stats.Stats
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class BreadFurnaceBlock: AbstractFurnaceBlock(Properties.copy(Blocks.FURNACE)), EntityBlock {
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
        )
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): net.minecraft.world.level.block.entity.BlockEntity
        = BlockEntity(pPos, pState)

    override fun <T : net.minecraft.world.level.block.entity.BlockEntity?> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<T>,
    ): BlockEntityTicker<T>? =
        createFurnaceTicker(pLevel, pBlockEntityType, BREAD_FURNACE_BLOCK_ENTITY_TYPE.get())

    override fun openContainer(pLevel: Level, pPos: BlockPos, pPlayer: Player) {
        val blockEntity = pLevel.getBlockEntity(pPos)
        if (blockEntity is BlockEntity) {
            pPlayer.openMenu(blockEntity)
            pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE)
        }
    }

    class BlockEntity(
        pPos: BlockPos,
        pBlockState: BlockState,
    ) : AbstractFurnaceBlockEntity(BREAD_FURNACE_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState, RecipeType.SMOKING) {
        override fun createMenu(pContainerId: Int, pInventory: Inventory): AbstractContainerMenu =
            Menu(pContainerId, pInventory)
        override fun getDefaultName(): Component = Component.translatable("container.bread_furnace")
    }

    class Menu(pContainerId: Int, pInventory: Inventory): AbstractFurnaceMenu(
        ModMenuTypes.BREAD_FURNACE.get(), ModRecipeTypes.BREAD_REFINEMENT, RecipeBookType.FURNACE, pContainerId, pInventory
    )
}