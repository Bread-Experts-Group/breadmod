package bread.mod.breadmod.neoforge

import bread.mod.breadmod.CommonUtils.invalidateCaps
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager

/**
 * NeoForge implementation of CommonUtils methods.
 *
 * @author Logan McLean
 * @since 1.0.0
 */
@Suppress("UNUSED")
object CommonUtilsImpl {
    /**
     * Invalidates all bock(entity) capabilities at [pos].
     * @see bread.mod.breadmod.CommonUtils.invalidateCaps
     */
    @JvmStatic
    fun Level.invalidateCaps(pos: BlockPos) {
        LogManager.getLogger().info("Invalidating capabilities at: $pos")
        invalidateCapabilities(pos)
    }
}