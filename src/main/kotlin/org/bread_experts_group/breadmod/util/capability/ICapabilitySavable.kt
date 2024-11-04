package org.bread_experts_group.breadmod.util.capability

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable

/**
 * A capability that can be saved to NBT, as well as notifying a callback whenever it is changed.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface ICapabilitySavable<out T : Tag> : INBTSerializable<@UnsafeVariance T> {
    /**
     * Callback that is called whenever the capability has changed.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    var changed: (() -> Unit)?
}