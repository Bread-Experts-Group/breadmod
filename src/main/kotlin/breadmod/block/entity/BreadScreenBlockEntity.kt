package breadmod.block.entity

import breadmod.registry.block.ModBlockEntities.BREAD_SCREEN
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class BreadScreenBlockEntity(pPos: BlockPos, pState: BlockState) : BlockEntity(BREAD_SCREEN.get(), pPos, pState) {

}