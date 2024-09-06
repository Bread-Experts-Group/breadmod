package bread.mod.breadmod.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.spi.StandardLevel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * A console appender that colors the output based on the log level.
 * @author Miko Elbrecht, Dan Dyer @ dandyer.co.uk (initial code source)
 * @since 1.0.0
 */
@Plugin(
    name = "ConsoleColorAppender",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE
)
class ConsoleColorAppender(
    name: String,
    filter: Filter?
) : AbstractAppender(name, filter, null, false, null) {
    internal companion object {
        /**
         * Factory method for creating a [ConsoleColorAppender].
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        @JvmStatic
        @PluginFactory
        fun createAppender(
            @PluginAttribute("name") name: String,
            @PluginElement("Filter") filter: Filter?
        ): ConsoleColorAppender = ConsoleColorAppender(name, filter)

        const val ESC = "\u001B["
        const val END = "m"

        const val FOREGROUND = 30
        const val BACKGROUND = 40
        const val BRIGHT = 60

        const val RESET = 0
        const val RED = 1
        const val GREEN = 2
        const val YELLOW = 3
        const val BLUE = 4

        // const val MAGENTA = 5
        const val CYAN = 6
        const val WHITE = 7

        private val threadColors = mutableMapOf<String, Pair<String, List<Int>>>()
        private var lastThreadColor: String? = null
    }

    private val colors = mapOf(
        StandardLevel.TRACE to ESC + (FOREGROUND + BRIGHT + RESET) + END,
        StandardLevel.DEBUG to ESC + (FOREGROUND + WHITE) + END,
        StandardLevel.INFO to ESC + (FOREGROUND + BRIGHT + GREEN) + END,
        StandardLevel.WARN to ESC + (FOREGROUND + BRIGHT + YELLOW) + END,
        StandardLevel.ERROR to ESC + (FOREGROUND + BRIGHT + RED) + END,
        StandardLevel.FATAL to ESC + (BACKGROUND + RED) + END
    )

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss;SSS")

    private fun String.prepend(len: Int): String {
        var new = ""
        val split = this.trim().split('\n')
        split.forEachIndexed { i, t ->
            if (i != 0) {
                new += " ,".padStart(len) + t + (if (i == split.size - 1) "" else '\n')
            } else new = t
        }
        return new
    }

    private fun String.getColorForString(): String {
        val color = threadColors[this]
        if (color != null) return color.first
        else {
            val last = threadColors[lastThreadColor]
            lastThreadColor = this

            val new =
                if (last == null)
                    (((ESC + (FOREGROUND + RED) + END) + this) + ESC + RESET + END) to mutableListOf(FOREGROUND + RED)
                else {
                    val newLast = last.second.toMutableList()
                    newLast[0] += 1
                    for (index in 0..newLast.size) {
                        val current = newLast.getOrNull(index) ?: break

                        /*if (current >= (FOREGROUND + BRIGHT + WHITE)) {
                            newLast[index] = FOREGROUND + RED
                            if (newLast.size == index + 1) newLast.add(FOREGROUND + RED)
                            else newLast[index + 1] += 1
                        } else */ if (/* current < (FOREGROUND + BRIGHT + RED) && */ current >= (FOREGROUND + WHITE)) {
                            newLast[index] = FOREGROUND /*+ BRIGHT*/ + RED
                            if (newLast.size == index + 1) newLast.add(FOREGROUND + RED)
                            else newLast[index + 1] += 1
                        } else break
                    }

                    var newStr = ""
                    for (index in this.indices) {
                        val localColor = newLast.getOrNull(index)
                        if (localColor != null) newStr += ESC + localColor + END + this[index]
                        else {
                            newStr += this.slice(index..<this.length)
                            break
                        }
                    }
                    (newStr + ESC + RESET + END) to newLast
                }

            threadColors[this] = new
            return new.first
        }
    }

    /**
     * Acts upon a given [LogEvent] for colorization.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("ReplacePrintlnWithLogging")
    override fun append(event: LogEvent?) {
        if (event != null && event.level.isMoreSpecificThan(Level.INFO)) {
            val formattedTime = LocalDateTime.ofEpochSecond(
                event.instant.epochSecond,
                event.instant.nanoOfSecond,
                ZoneOffset.UTC
            ).format(formatter)

            val prepend = "[${ESC + (FOREGROUND + BRIGHT + BLUE) + END}${formattedTime}${ESC + RESET + END}" +
                    "/${colors[event.level.standardLevel]}${event.level.toString().padEnd(5)}${ESC + RESET + END}] " +
                    "[${event.threadName.getColorForString()}/" + "${event.loggerName.getColorForString()}]"

            event.thrownProxy.let {
                val prependStrippedLength = prepend.replace(Regex("\u001B\\[.+?m"), "").length
                println("$prepend ${event.message.formattedMessage.prepend(prependStrippedLength)}")
                if (it != null) println(it.extendedStackTraceAsString.prepend(prependStrippedLength))
            }
        }
    }
}