package breadmod.block.entity.storage

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.block.util.ModBlockStateProperties
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.CapabilityHolder.Companion.ACCEPT_ALL
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities

class EnergyStorageBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity<EnergyStorageBlockEntity>(
    ModBlockEntityTypes.ENERGY_STORAGE.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.ENERGY to (EnergyBattery(
        1000000, 50000, 50000
    ) to ACCEPT_ALL)
) {

    override fun tick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<EnergyStorageBlockEntity>
    ) {
        val energyLevel = pBlockEntity.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY) ?: return

        when(energyLevel.energyStored) {
            250000 -> setStorageLevel(pState, pLevel, pPos, 1)
            500000 -> setStorageLevel(pState, pLevel, pPos, 2)
            750000 -> setStorageLevel(pState, pLevel, pPos, 3)
            1000000 -> setStorageLevel(pState, pLevel, pPos, 4)
        }

        val holder = capabilityHolder.capabilities[ForgeCapabilities.ENERGY]
        val battery = ((holder ?: return).first.resolve().get() as EnergyBattery)

        if(battery.stored > 0) {
            battery.distribute(
                pLevel, pPos, holder.second,
                pState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite
            )
        }
    }

    private fun setStorageLevel(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pValue: Int
    ) = pLevel.setBlockAndUpdate(pPos, pState.setValue(ModBlockStateProperties.STORAGE_LEVEL, pValue))
}