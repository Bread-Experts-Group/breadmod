package breadmod.item.rendering

import net.minecraft.client.Minecraft
import net.minecraft.util.Mth

object TimerTicker {
    private val instance = Minecraft.getInstance()
    private var lastRotation = 0f
    var rotation = 0f

    fun tick() {
        if(!instance.isPaused) {
            if(rotation <= 360f) { // todo smoothed out rotation | WrenchItemRenderer.java
                lastRotation = rotation
                rotation = 2f + Mth.lerp(instance.frameTime, lastRotation, rotation)
//                rotation += 0.1f
                println("current timer tick: $rotation")
//                println("${instance.partialTick}")
            } else rotation = 0f
        }
    }
}