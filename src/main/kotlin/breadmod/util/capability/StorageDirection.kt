package breadmod.util.capability

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid

/**
 * "Direction" of a storage object when evaluating methods for item/fluid operations
 * @see BIDIRECTIONAL
 * @see STORE_ONLY
 * @see EMPTY_ONLY
 * @author Miko Elbrecht
 * @since 1.0.0
 */
enum class StorageDirection {
    /**
     * Bidirectional. This storage object will accept any direction of unit transit from fill/drain (for [Fluid]s) or insert/extract (for [ItemStack])
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    BIDIRECTIONAL,

    /**
     * Store-only. This storage object will only respond to fill/insert calls.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    STORE_ONLY,

    /**
     * Empty-only. This storage object will only respond to drain/extract calls.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    EMPTY_ONLY
}