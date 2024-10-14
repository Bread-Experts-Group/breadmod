package bread.mod.breadmod

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager

/**
 * Helper methods for NeoForge specific actions.
 *
 * @author Logan McLean
 * @since 1.0.0
 */
@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
object CommonUtils {
    private val logger = LogManager.getLogger()

    /**
     * Invalidates all block(entity) capabilities at [pos].
     */
    @JvmStatic
    @ExpectPlatform
    fun Level.invalidateCaps(pos: BlockPos) {
        logger.error("invalidateCaps is not available on this platform.")
    }
}