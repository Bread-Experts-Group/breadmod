package bread.mod.breadmod.item.tool_gun

import bread.mod.breadmod.util.render.rgMinecraft
import net.minecraft.util.RandomSource

// todo convert this to use millis and merge into the renderer (look in WarOverlay for reference)
@Suppress("MemberVisibilityCanBePrivate")
object ToolGunAnimationHandler {
    var coilRotation: Float = 0f
    var coilDelta: Float = 0f

    var recoil: Float = 0f

    private val random = RandomSource.create()

    fun clientTick() {
        if (!rgMinecraft.isPaused) {
            coilRotation += 2f * coilDelta
            if (coilDelta > 0f) coilDelta -= 0.1f * rgMinecraft.timer.realtimeDeltaTicks / 1.05f else coilDelta = 0f
            if (recoil > 0f) recoil -= 0.05f * (rgMinecraft.timer.realtimeDeltaTicks / 10) else recoil = 0f
            coilRotation %= 360
        }
    }

    fun trigger() {
        coilDelta = 4f + random.nextFloat()
        recoil = 0.1f
    }
}