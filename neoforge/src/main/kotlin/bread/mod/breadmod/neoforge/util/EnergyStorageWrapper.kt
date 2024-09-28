package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.forge_handlers.energy.ArchEnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage

class EnergyStorageWrapper(
    val energyStorage: ArchEnergyStorage
) : IEnergyStorage {
    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int =
        energyStorage.receiveEnergy(toReceive, simulate)

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int =
        energyStorage.extractEnergy(toExtract, simulate)

    override fun getEnergyStored(): Int = energyStorage.getEnergyStored()
    override fun getMaxEnergyStored(): Int = energyStorage.getMaxEnergyStored()
    override fun canExtract(): Boolean = energyStorage.canExtract()
    override fun canReceive(): Boolean = energyStorage.canReceive()
}