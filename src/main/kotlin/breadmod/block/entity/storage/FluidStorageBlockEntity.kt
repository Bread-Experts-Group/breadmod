package breadmod.block.entity.storage

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.CapabilityHolder.Companion.ACCEPT_ALL
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.templates.FluidTank

class FluidStorageBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity<FluidStorageBlockEntity>(
    ModBlockEntityTypes.FLUID_STORAGE.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(
        mutableMapOf(
            FluidTank(50000) to StorageDirection.BIDIRECTIONAL
        )
    ) to ACCEPT_ALL)
) {
    fun fluidHandler() = capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)
}