package breadmod.block.machine.multiblock.generic.entity

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class PowerInterfaceBlockEntity(pPos: BlockPos, pBlockState: BlockState) : AbstractMachineBlockEntity.Powered<PowerInterfaceBlockEntity>(
    ModBlockEntities.MULTIBLOCK_GENERIC_POWER.get(), pPos, pBlockState,
    EnergyBattery(750000, 1000) to null
)