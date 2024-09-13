package bread.mod.breadmod.util

interface EnergyHandler {
    fun receiveEnergy(toReceive: Int, simulate: Boolean): Int

    fun extractEnergy(toExtract: Int, simulate: Boolean): Int

    fun getEnergyStored(): Int

    fun getMaxEnergyStored(): Int

    fun canExtract(): Boolean

    fun canReceive(): Boolean
}