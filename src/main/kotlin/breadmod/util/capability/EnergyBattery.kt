package breadmod.util.capability

import breadmod.util.translateDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.energy.IEnergyStorage
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

/**
 * Basic energy storage implementation with distribution.
 * @author Miko Elbrecht
 * @since 1.0.0.
 */
class EnergyBattery(
    capacity: Int,
    /**
     * The maximum amount of energy that can be inserted into this battery per receiveEnergy operation.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var maxInputPerOp: Int = Int.MAX_VALUE,
    /**
     * The maximum amount of energy that can be extracted from this battery per extractEnergy operation.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var maxOutputPerOp: Int = Int.MAX_VALUE,
    override var changed: (() -> Unit)? = null
) : IEnergyStorage, ICapabilitySavable<CompoundTag> {
    constructor(capacity: Int, bTransportLimit: Int = Int.MAX_VALUE, changed: (() -> Unit)? = null) : this(
        capacity,
        bTransportLimit,
        bTransportLimit,
        changed
    )

    /**
     * The amount of energy currently stored within this battery.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var stored: Int = 0

    /**
     * The maximum amount of energy that can be stored within this battery.
     * Changing this value will remove any excess energy.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var capacity: Int = capacity
        set(value) {
            stored = min(stored, value); field = value
        }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = if (maxInputPerOp > 0) {
        val toReceive = min(maxReceive, maxInputPerOp)
        val newStore = stored + min(maxReceive, maxInputPerOp)
        val delta = if (newStore > capacity) min(capacity - stored, newStore) else toReceive
        if (!simulate && delta != 0) {
            stored += delta
            changed?.invoke()
        }
        delta
    } else 0

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = if (maxOutputPerOp > 0) {
        val delta = min(stored, min(maxExtract, maxOutputPerOp))
        if (!simulate && delta != 0) {
            stored -= delta
            changed?.invoke()
        }
        delta
    } else 0

    override fun getEnergyStored(): Int = stored
    override fun getMaxEnergyStored(): Int = capacity
    override fun canExtract(): Boolean = maxOutputPerOp > 0
    override fun canReceive(): Boolean = maxInputPerOp > 0

    override fun serializeNBT(): CompoundTag = CompoundTag().also {
        it.putInt(STORED_TAG, stored)
        it.putInt(CAPACITY_TAG, capacity)
        it.putInt(MAX_EXTRACT_TAG, maxOutputPerOp)
        it.putInt(MAX_RECEIVE_TAG, maxInputPerOp)
    }

    override fun deserializeNBT(nbt: CompoundTag?) {
        nbt?.let {
            capacity = it.getInt(CAPACITY_TAG)
            maxOutputPerOp = it.getInt(MAX_EXTRACT_TAG)
            maxInputPerOp = it.getInt(MAX_RECEIVE_TAG)
            stored = it.getInt(STORED_TAG)
            changed?.invoke()
        }
    }

    fun distribute(pLevel: Level, pPos: BlockPos, sides: List<Direction?>?, facing: Direction = Direction.NORTH) {
        val energies = (sides?.filterNotNull() ?: Direction.entries).mapNotNull {
            val rotated = translateDirection(facing, it).let { r -> if (r.axis == Direction.Axis.Z) r.opposite else r }
            pLevel.getBlockEntity(pPos.offset(rotated.normal))?.getCapability(
                ForgeCapabilities.ENERGY,
                rotated.opposite
            )?.resolve()?.getOrNull()
        }

        if (energies.isNotEmpty()) {
            val toDistribute = this.extractEnergy(maxOutputPerOp, true) / energies.size
            var distributed = 0
            energies.forEach { distributed += it.receiveEnergy(toDistribute, false) }
            this.extractEnergy(distributed, false)
        }
    }

    internal companion object {
        const val STORED_TAG = "stored"
        const val CAPACITY_TAG = "capacity"
        const val MAX_EXTRACT_TAG = "maxExtract"
        const val MAX_RECEIVE_TAG = "maxReceive"
    }
}