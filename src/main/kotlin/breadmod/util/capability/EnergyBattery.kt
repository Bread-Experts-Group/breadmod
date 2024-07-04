package breadmod.util.capability

import net.minecraft.nbt.CompoundTag
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.min

class EnergyBattery(
    capacity: Int, var bMaxReceive: Int = Int.MAX_VALUE, var bMaxExtract: Int = Int.MAX_VALUE,
    override var changed: (() -> Unit)? = null
) : IEnergyStorage, ICapabilitySavable<CompoundTag> {
    constructor(capacity: Int, bTransportLimit: Int = Int.MAX_VALUE, changed: (() -> Unit)? = null): this(capacity, bTransportLimit, bTransportLimit, changed)

    var stored: Int = 0
    var capacity: Int = capacity
        set(value) { stored = min(stored, value); field = value }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = if(bMaxReceive > 0) {
        val toReceive = min(maxReceive, bMaxReceive)
        val newStore = stored + min(maxReceive, bMaxReceive)
        val delta = if(newStore > capacity) min(capacity - stored, newStore) else toReceive
        if(!simulate && delta != 0) {
            stored += delta
            changed?.invoke()
        }
        delta
    } else 0

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = if(bMaxExtract > 0) {
        val delta = min(stored, min(maxExtract, bMaxExtract))
        if(!simulate && delta != 0) {
            stored -= delta
            changed?.invoke()
        }
        delta
    } else 0

    override fun getEnergyStored(): Int = stored
    override fun getMaxEnergyStored(): Int = capacity
    override fun canExtract(): Boolean = bMaxExtract > 0
    override fun canReceive(): Boolean = bMaxReceive > 0

    override fun serializeNBT(): CompoundTag = CompoundTag().also {
        it.putInt(STORED_TAG, stored)
        it.putInt(CAPACITY_TAG, capacity)
        it.putInt(MAX_EXTRACT_TAG, bMaxExtract)
        it.putInt(MAX_RECEIVE_TAG, bMaxReceive)
    }

    override fun deserializeNBT(nbt: CompoundTag?) {
        nbt?.let {
            capacity = it.getInt(CAPACITY_TAG)
            bMaxExtract = it.getInt(MAX_EXTRACT_TAG)
            bMaxReceive = it.getInt(MAX_RECEIVE_TAG)
            stored = it.getInt(STORED_TAG)
            changed?.invoke()
        }
    }

    internal companion object {
        const val STORED_TAG = "stored"
        const val CAPACITY_TAG = "capacity"
        const val MAX_EXTRACT_TAG = "maxExtract"
        const val MAX_RECEIVE_TAG = "maxReceive"
    }
}