package org.bread_experts_group.breadmod.util.capability

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.util.ObservableList
import org.bread_experts_group.breadmod.util.translateDirection
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

// todo update javadoc

/**
 * A [ICapabilitySavable] and its list of valid sides.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
typealias CapabilityContainer = Pair<ICapabilitySavable<Tag>, List<Direction?>>

/**
 * Holds a [Map] of [BaseCapability]s, linking to a pair of actual capability objects and the valid sides they can be applied to.
 * This class is for convenience.
 *
 * @param passedCapabilities The list of capabilities and their "actual" values.
 * The direction list contains directions relative to the "north" of this object this is attached to.
 * If there is a null value within the list, it will accept "internal/self" sides.
 *
 * @param changed The callback to be called when the sides of a capability are changed.
 *
 * @see net.minecraftforge.common.capabilities.ICapabilityProvider.getCapability
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class CapabilityHolder(passedCapabilities: Map<BaseCapability<*, *>, CapabilityContainer>) {

    /**
     * The capabilities this [CapabilityHolder] is keeping track of.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capabilitySided
     * @see capability
     * @see capabilityOrNull
     * @see invalidate
     */
    val capabilities: Map<BaseCapability<*, *>, Pair<Optional<ICapabilitySavable<Tag>>, ObservableList<Direction?>>> =
        passedCapabilities.mapValues { Optional.of(it.value.first) to ObservableList(it.value.second) }

    var changed: ((capability: BaseCapability<*, *>, index: Int, element: Direction?) -> Unit)? = null
        set(value) {
            if (value != null) {
                capabilities.forEach { (t, u) ->
                    val localChanged = { index: Int, element: Direction? -> value.invoke(t, index, element) }
                    u.second.listChanged = localChanged
                }
            }
            field = value
        }

    /**
     * Retrieves the current sidedness list of the specified [capability] – if it is present.
     * A returned value of null may indicate that either: the specified [capability] is not present,
     * or it accepts any side, including internal.
     * @see CapabilityHolder
     */
    fun getSidedness(capability: BaseCapability<*, *>): ObservableList<Direction?>? = capabilities[capability]?.second

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
    @Suppress("UNCHECKED_CAST") // todo uh oh
    fun <T, R> capabilitySided(
        capability: BaseCapability<T, R>,
        translateFor: Direction,
        side: Direction?
    ): Optional<T>? {
        val translatedSide = if (side != null) translateDirection(translateFor, side) else null
        capabilities.forEach { (myCapability, pair) ->
            if (myCapability == capability && pair.second.contains(translatedSide))
                return pair.first as Optional<T>
        }
        return null
    }

    /**
     * Retrieves a capability [T] for the specified [capability].
     * This method will not throw a [NullPointerException] if the capability doesn't exist at the time of calling,
     * but will return null.
     * @param T The instance of type [T] to search for, as indexed by [capability]
     * @param capability The capability to search an instance of [T] for
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capability
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> capabilityOrNull(capability: BaseCapability<*, *>): T? {
        capabilities.forEach { (myCapability, pair) ->
            if (myCapability == capability) return pair.first.getOrNull() as T?
        }
        return null
    }

    /**
     * Retrieves a capability [T] for the specified [capability].
     * If the capability is unavailable (doesn't exist at the time of calling),
     * this method will throw a [NullPointerException].
     * @param T The instance of type [T] to search for, as indexed by [capability]
     * @param capability The capability to search an instance of [T] for
     * @throws NullPointerException If [T] for [capability] does not exist
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see capabilityOrNull
     */
    fun <T> capability(capability: BaseCapability<*, *>): T =
        capabilityOrNull<T>(capability) ?: throw NullPointerException("Capability unavailable")

    // todo invalidate exists only in Level and BlockEntity
//    /**
//     * Invalidates all stored capability optionals.
//     * @author Miko Elbrecht
//     * @since 1.0.0
//     */
//    fun invalidate(): Unit = capabilities.forEach { _, (optional, _) -> optional.invalidate() }

    /**
     * Serializes this list of [INBTSerializable] capabilities into the specified [tag].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun serialize(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag = tag.also {
        capabilities.forEach { (capability, actual) ->
            actual.first.ifPresent {
                tag.put(
                    capability.name().toString(),
                    it.serializeNBT(registries)
                )
            }
        }
    }

    /**
     * Deserializes this list of [INBTSerializable] capabilities from the specified [tag].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun deserialize(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag = tag.also {
        capabilities.forEach { (capability, actual) ->
            actual.first.ifPresent { actualPresent ->
                tag.get(capability.name().toString())?.let { tag -> actualPresent.deserializeNBT(registries, tag) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Tag> INBTSerializable<T>.deserializeNBT(tag: Tag?) {
        if (tag != null) this.deserializeNBT(tag as T)
    }

    /**
     * Distributes all capabilities inside this holder's contents to surrounding blocks.
     *
     * @see [ICapabilityDistributable]
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun distribute(pLevel: Level, pPos: BlockPos, pFacing: Direction) {
        capabilities.forEach { (_, actual) ->
            actual.first.ifPresent {
                if (it is ICapabilityDistributable) it.distribute(pLevel, pPos, actual.second, pFacing)
            }
        }
    }

    companion object {
        val ACCEPT_ALL: List<Direction?> = listOf(*Direction.entries.toList().toTypedArray(), null)
    }
}