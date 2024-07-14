package breadmod.block.machine.multiblock.generic.entity

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

class PowerInterfaceBlockEntity(pPos: BlockPos, pBlockState: BlockState) : AbstractMachineBlockEntity<PowerInterfaceBlockEntity>(
    ModBlockEntities.MULTIBLOCK_GENERIC_POWER.get(), pPos, pBlockState,
     ForgeCapabilities.ENERGY to (EnergyBattery(750000, 1000) to null)
)