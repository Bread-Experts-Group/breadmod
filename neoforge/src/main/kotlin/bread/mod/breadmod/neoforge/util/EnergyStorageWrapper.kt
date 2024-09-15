package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.ArchEnergyHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.energy.IEnergyStorage

class EnergyStorageWrapper(
    level: Level,
    pos: BlockPos
) : IEnergyStorage {
    val energy =
        level.getBlockEntity(pos) as ArchEnergyHandler? ?: throw IllegalStateException("no energy handler at $pos")

    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int =
        energy.receiveEnergy(toReceive, simulate)

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int =
        energy.extractEnergy(toExtract, simulate)

    override fun getEnergyStored(): Int = energy.getEnergyStored()
    override fun getMaxEnergyStored(): Int = energy.getMaxEnergyStored()
    override fun canExtract(): Boolean = energy.canExtract()
    override fun canReceive(): Boolean = energy.canReceive()
}