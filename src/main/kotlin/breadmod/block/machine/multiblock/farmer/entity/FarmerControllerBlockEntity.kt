package breadmod.block.machine.multiblock.farmer.entity

import breadmod.registry.block.ModBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class FarmerControllerBlockEntity( // todo.. everything
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntityTypes.FARMER_CONTROLLER.get(), pPos, pBlockState) {
}