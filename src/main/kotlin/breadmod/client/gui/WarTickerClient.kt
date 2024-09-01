package breadmod.client.gui

object WarTickerClient {
    var timer = 30
    var isIncreasing = false
    var increasingTimer = 0
    var active = false

    var scroll = -110f
    var lastScroll = -110f

    fun tick() {
        if (increasingTimer > 0) { // Increase timer
            increasingTimer--
            timer++
        } else isIncreasing = false

        lastScroll = scroll // hell code until I figure out proper smooth like anims from minecraft's code
        if (scroll > -110.0 && !active) {
            scroll -= (1f * 0.5f) * 4.0f
        } else if (scroll < 0.0 && active) {
            scroll += (1f * 0.5f) * 4.0f
        }
    }
}