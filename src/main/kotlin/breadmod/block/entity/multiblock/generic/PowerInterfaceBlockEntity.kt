package breadmod.block.entity.multiblock.generic

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.CapabilityHolder.Companion.ACCEPT_ALL
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

class PowerInterfaceBlockEntity(pPos: BlockPos, pBlockState: BlockState) :
    AbstractMachineBlockEntity<PowerInterfaceBlockEntity>(
        ModBlockEntityTypes.MULTIBLOCK_GENERIC_POWER.get(), pPos, pBlockState,
        ForgeCapabilities.ENERGY to (EnergyBattery(750000, 1000) to ACCEPT_ALL)
    )