package breadmod.client.render.tool_gun

import net.minecraft.client.Minecraft
import net.minecraft.util.RandomSource

@Suppress("MemberVisibilityCanBePrivate")
object ToolGunAnimationHandler {
    var coilRotation: Float = 0f
    var coilDelta: Float = 0f

    var recoil: Float = 0f

    private val random = RandomSource.create()

    fun clientTick() {
        val instance = Minecraft.getInstance()
        if(!instance.isPaused) {
            coilRotation += 2f * coilDelta
            if(coilDelta > 0f) coilDelta -= 0.1f * instance.partialTick / 1.05f else coilDelta = 0f
            if(recoil > 0f) recoil -= 0.05f * (instance.partialTick / 10) else recoil = 0f
            coilRotation %= 360
        }
    }

    fun trigger() {
        coilDelta = 4f + random.nextFloat()
        recoil = 0.1f
    }
}