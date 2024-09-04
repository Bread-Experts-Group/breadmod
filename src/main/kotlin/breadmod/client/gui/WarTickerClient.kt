package breadmod.client.gui

object WarTickerClient {
    var timeLeft = 30
    var isTimerIncreasing = false
    var increasingTimer = 0
    var timerActive = false

    var timerPosition = -110f
    var lastTimerPosition = -110f

    fun tick() {
        if (increasingTimer > 0) { // Increase timer
            increasingTimer--
            timeLeft++
        } else isTimerIncreasing = false

        lastTimerPosition = timerPosition // hell code until I figure out proper smooth like anims from minecraft's code
        if (timerPosition > -110.0 && !timerActive) {
            timerPosition -= (1f * 0.5f) * 4.0f
        } else if (timerPosition < 0.0 && timerActive) {
            timerPosition += (1f * 0.5f) * 4.0f
        }
    }
}