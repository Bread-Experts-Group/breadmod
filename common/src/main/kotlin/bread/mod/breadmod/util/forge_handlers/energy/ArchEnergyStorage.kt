package bread.mod.breadmod.util.forge_handlers.energy

import kotlin.math.min

// todo energy sources that push out Int.MAX_VALUE cause the energy buffer in our block to rapidly flip flop from being empty to holding MAX_VALUE,
//  maybe use the extract/receive logic from NeoForge's energy storage?
/**
 * Mirrored implementation of NeoForge's Energy Storage in common
 */
open class ArchEnergyStorage(
    var capacity: Int,
    var maxReceive: Int,
    var maxExtract: Int,
    var energy: Int
) : ArchEnergyHandler {
    constructor(capacity: Int) : this(capacity, capacity, capacity, 0)
    constructor(capacity: Int, maxReceive: Int) : this(capacity, maxReceive, capacity, 0)
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int) : this(capacity, maxReceive, maxExtract, 0)

    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int = if (canReceive()) {
        val toReceive = min(toReceive, maxReceive)
        val newStore = energy + min(toReceive, maxReceive)
        val delta = if (newStore > capacity) min(capacity - energy, newStore) else toReceive
        if (!simulate && delta != 0) {
            energy += delta
        }
        delta
    } else 0

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int = if (canExtract()) {
        val delta = min(energy, min(toExtract, maxExtract))
        if (!simulate && delta != 0) {
            energy -= delta
        }
        delta
    } else 0

    override fun getEnergyStored(): Int = energy
    override fun getMaxEnergyStored(): Int = capacity
    override fun canExtract(): Boolean = maxExtract > 0
    override fun canReceive(): Boolean = maxReceive > 0
}