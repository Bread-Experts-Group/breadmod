package breadmod.util.render

import net.minecraft.client.Minecraft

object TimerTicker {
    private val instance = Minecraft.getInstance()
    //private var lastRotation = 0f
    var rotation = 0f
    private var ticks = 0.0f

    fun tick() {
        if(!instance.isPaused) {
            ticks += 1.0f % 1728000
//            if(rotation <= 360f) { // todo smoothed out rotation | WrenchItemRenderer.java
//                lastRotation = rotation
//                rotation = 2f + lerp(instance.frameTime, lastRotation, rotation)
//            } else rotation = 0f


        }
    }

    fun getRenderTime(): Float = ticks + instance.partialTick
    //fun reset() { ticks = 0f; rotation = 0f }
}