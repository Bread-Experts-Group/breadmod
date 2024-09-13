package bread.mod.breadmod.neoforge.util

import bread.mod.breadmod.util.EnergyHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.neoforged.neoforge.energy.IEnergyStorage

// todo it "works" but it voids all energy inserted
class EnergyStorageWrapper(
    val container: Container,
    level: Level,
    pos: BlockPos
) : IEnergyStorage {
    val energy =
        level.getBlockEntity(pos) as EnergyHandler? ?: throw IllegalArgumentException("no energy handler at $pos")

    override fun receiveEnergy(i: Int, bl: Boolean): Int {
        container.setChanged()
        return energy.receiveEnergy(i, false)
    }

    override fun extractEnergy(i: Int, bl: Boolean): Int {
        container.setChanged()
        return energy.extractEnergy(i, false)
    }

    override fun getEnergyStored(): Int = energy.getEnergyStored()
    override fun getMaxEnergyStored(): Int = energy.getMaxEnergyStored()
    override fun canExtract(): Boolean = energy.canExtract()
    override fun canReceive(): Boolean = energy.canReceive()
}