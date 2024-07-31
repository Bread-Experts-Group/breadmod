package breadmod.client.render.tool_gun

import net.minecraft.client.Minecraft
import net.minecraft.util.RandomSource

@Suppress("MemberVisibilityCanBePrivate")
object ToolGunAnimationHandler {
    private var coilSpinDelta: Float = 0f
    var coilRotation: Float = 0f
    private var ticks: Int = 0

    private val random = RandomSource.create()

    fun tick() {
        val instance = Minecraft.getInstance()
        if(!instance.isPaused) {
            ticks += (ticks + 1) % 1_728_000

            if(coilSpinDelta > 0f) coilSpinDelta -= 0.1f * instance.partialTick else coilSpinDelta = 0f
            coilRotation += coilSpinDelta // Choppy Rotation, look at WorldShaperItemRenderer to figure out how to smooth it
            coilRotation %= 360
        }
    }

    fun trigger() {
        coilSpinDelta = 8f + random.nextFloat()
    }

    fun getRenderTime(): Float {
        val instance = Minecraft.getInstance()
        return ticks + instance.frameTime
    }
}