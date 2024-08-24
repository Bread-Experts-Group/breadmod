package breadmod.client.gui

import breadmod.registry.sound.ModSounds
import breadmod.util.render.minecraft

object WarTicker {
    var timer = 200
    var ticker = 41
    var isIncreasing = false
    var increasingTimer = 0
    var active = false

    fun tick() {
        val player = minecraft.player ?: return
        if (!minecraft.isPaused && active) {
            if (!isIncreasing) {
                if (ticker == 0 && timer > 0) { // Tick timer down by one
                    player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 1f)
                    timer --
                    ticker = 41
                } else if (timer > 0) ticker -- // Tick down ticker
            }
            if (increasingTimer > 0) { // Increase timer
                increasingTimer --
                timer++
                ticker = 41
            } else isIncreasing = false
        }
    }

    fun reset() {
        timer = 30
        ticker = 41
    }

    fun increaseTimer(pAmount: Int = 41) {
        val player = minecraft.player ?: return
        player.playSound(ModSounds.WAR_TIMER_UP.get(), 1f, 1f)
        increasingTimer += pAmount
        isIncreasing = true
    }
}