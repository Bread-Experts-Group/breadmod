package breadmod.block.entity

import breadmod.registry.block.ModBlockEntityTypes.MONITOR
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class BreadScreenBlockEntity(pPos: BlockPos, pState: BlockState) : BlockEntity(MONITOR.get(), pPos, pState)