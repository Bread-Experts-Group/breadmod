package breadmod.client.gui

import breadmod.registry.sound.ModSounds
import breadmod.util.render.rgMinecraft

object WarTicker {
    var timer = 30
    var ticker = 41
    var isIncreasing = false
    var increasingTimer = 0
    var active = false

    var scroll = -110f
    var lastScroll = -110f

    fun tick() {
        val player = rgMinecraft.player ?: return
        if (!rgMinecraft.isPaused && active) {
            if (!isIncreasing) {
                if (ticker == 0 && timer > 0) { // Tick timer down by one
                    player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 1f)
                    timer--
                    ticker = 41
                } else if (timer > 0) ticker-- // Tick down ticker
            }
            if (increasingTimer > 0) { // Increase timer
                increasingTimer--
                timer++
                ticker = 41
            } else isIncreasing = false
        }
        if (!rgMinecraft.isPaused) {
            lastScroll = scroll // hell code until I figure out proper smooth like anims from minecraft's code
            if (scroll > -110.0 && !active) {
                scroll -= (1f * 0.5f) * 4.0f
            } else if (scroll < 0.0 && active) {
                scroll += (1f * 0.5f) * 4.0f
            }
        }
    }

    fun reset() {
        timer = 30
        ticker = 41
    }

    fun increaseTimer(pAmount: Int = 41) {
        val player = rgMinecraft.player ?: return
        player.playSound(ModSounds.WAR_TIMER_UP.get(), 1f, 1f)
        increasingTimer += pAmount
        isIncreasing = true
    }
}