package bread.mod.breadmod.client

object WarTickerClient {
    var timeLeft = 30
    var isTimerIncreasing = false
    var increasingTimer = 0
    var timerActive = true

    fun tick() {
        if (increasingTimer > 0) { // Increase timer
            increasingTimer--
            timeLeft++
        } else isTimerIncreasing = false
    }
}