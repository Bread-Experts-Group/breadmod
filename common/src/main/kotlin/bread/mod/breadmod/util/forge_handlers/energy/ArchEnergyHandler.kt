package bread.mod.breadmod.util.forge_handlers.energy

/**
 * Bridge interface for NeoForge's energy capability.
 * @since 1.0.0
 */
interface ArchEnergyHandler {

    fun receiveEnergy(toReceive: Int, simulate: Boolean): Int

    fun extractEnergy(toExtract: Int, simulate: Boolean): Int

    fun getEnergyStored(): Int

    fun getMaxEnergyStored(): Int

    fun canExtract(): Boolean

    fun canReceive(): Boolean
}