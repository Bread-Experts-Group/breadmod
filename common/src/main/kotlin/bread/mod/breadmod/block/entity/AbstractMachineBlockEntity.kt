package bread.mod.breadmod.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

// todo try to port over the features from the 1.20 branch (this is gonna be hard without platform specific apis)
abstract class AbstractMachineBlockEntity<T: AbstractMachineBlockEntity<T>>(
    type: BlockEntityType<T>,
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(type, pos, blockState) {

    open fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: AbstractMachineBlockEntity<T>) {
    }
}