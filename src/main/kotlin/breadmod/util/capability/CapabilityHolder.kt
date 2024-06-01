package breadmod.util.capability

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional
import kotlin.jvm.optionals.getOrNull

/**
 * Holds a [Map] of [Capability]s, linking to a pair of actual capability objects and the valid sides they can be applied to.
 * This class is for convenience.
 *
 * @param passedCapabilities The list of capabilities and their "actual" values.
 * The direction list is optional, meaning that this capability accepts all sides. Otherwise, it is directions relative to the "north"
 * of this object this is attached to. If there is a null value within the list, it will accept "internal/self" sides.
 * @see net.minecraftforge.common.capabilities.ICapabilityProvider.getCapability
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class CapabilityHolder(passedCapabilities: Map<Capability<*>, Pair<INBTSerializable<out Tag>, MutableList<Direction?>?>>) {
    /**
     * The capabilities this [CapabilityHolder] is keeping track of.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capabilitySided
     * @see capability
     * @see capabilityOrNull
     * @see invalidate
     */
    private val capabilities = passedCapabilities.mapValues {
        LazyOptional.of { it.value.first } to it.value.second
    }

    /**
     * Retrieves the current sidedness list of the specified [capability] - if it's present.
     * A returned value of null may indicate that either: the specified [capability] is not present, or it accepts any side, including internal.
     * @see CapabilityHolder
     */
    fun getSidedness(capability: Capability<*>): MutableList<Direction?>? = capabilities[capability]?.second

    /**
     * Casts a [LazyOptional] which matches the specified [capability] for a [side], as translated by [translateFor].
     * If such a [LazyOptional] does not exist, this method will return null.
     * @param T The instance of type [T] to cast for
     * @param capability The capability to search for
     * @param translateFor How [side] should be translated according to
     * @param side The side for which this capability is being checked for (or null or internal)
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capability
     * @see capabilityOrNull
     */

    // TODO TRANSLATEFOR
    fun <T> capabilitySided(capability: Capability<T>, translateFor: Direction, side: Direction?): LazyOptional<T>? {
        capabilities.forEach { (myCapability, pair) ->
            if(myCapability == capability && (pair.second == null || (pair.second ?: return@forEach).contains(side))) return pair.first.cast()
        }
        return null
    }

    /**
     * Retrieves a capability [T] for the specified [capability]. This method will not throw a [NullPointerException] if the capability
     * does not exist at the time of calling, but will return null.
     * @param T The instance of type [T] to search for, as indexed by [capability]
     * @param capability The capability to search an instance of [T] for
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capability
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> capabilityOrNull(capability: Capability<*>): T? {
        capabilities.forEach { (myCapability, pair) -> if(myCapability == capability) return pair.first.resolve().getOrNull() as T? }
        return null
    }

    /**
     * Retrieves a capability [T] for the specified [capability]. If the capability is unavailable (does not exist at the time of calling),
     * this method will throw a [NullPointerException].
     * @param T The instance of type [T] to search for, as indexed by [capability]
     * @param capability The capability to search an instance of [T] for
     * @throws NullPointerException If [T] for [capability] does not exist
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capabilityOrNull
     */
    fun <T> capability(capability: Capability<*>): T = capabilityOrNull<T>(capability) ?: throw NullPointerException("Capability unavailable")

    /**
     * Invalidates all stored capability optionals.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun invalidate() = capabilities.forEach { _, (optional) -> optional.invalidate() }

    /**
     * Serializes this list of [INBTSerializable] capabilities into the specified [tag].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun serialize(tag: CompoundTag) = tag.also { capabilities.forEach { (capability, actual) -> actual.first.ifPresent { tag.put(capability.name, it.serializeNBT()) } } }

    /**
     * Deserializes this list of [INBTSerializable] capabilities from the specified [tag].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun deserialize(tag: CompoundTag) = tag.also { capabilities.forEach { (capability, actual) -> actual.first.ifPresent { it.deserializeNBT(tag.get(capability.name)) } } }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Tag> INBTSerializable<T>.deserializeNBT(tag: Tag?) { if(tag != null) this.deserializeNBT(tag as T) }
}