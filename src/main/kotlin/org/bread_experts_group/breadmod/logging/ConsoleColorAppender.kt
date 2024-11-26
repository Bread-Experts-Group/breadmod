package org.bread_experts_group.breadmod.logging

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
import org.apache.logging.log4j.core.impl.ThrowableProxy
import org.apache.logging.log4j.spi.StandardLevel
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
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

        //        const val MAGENTA = 5
        const val CYAN = 6
        const val WHITE = 7

        private val threadColorBanks = mutableMapOf<ColorBanks, Pair<MutableMap<String, Pair<String, List<Int>>>, String?>>()

        private val DEFAULT_OUT = PrintStream(FileOutputStream(FileDescriptor.out))

        private const val BG = ESC + BACKGROUND + END

        private const val EBG = (ESC + (BRIGHT + RED + BACKGROUND) + END) + (ESC + FOREGROUND + END)
        private const val RBG = (ESC + (RED + BACKGROUND) + END) + (ESC + FOREGROUND + END)

        private const val S_EBG = (ESC + (BRIGHT + BACKGROUND) + END) + (ESC + FOREGROUND + END)
        private const val S_RBG = (ESC + (WHITE + BACKGROUND) + END) + (ESC + FOREGROUND + END)
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

    private enum class ColorBanks {
        FILE,
        CLASSLOADER,
        LOGGER,
        THREAD
    }

    private fun String.getColorForString(bank: ColorBanks): String {
        val (colorBank, lastColor) = threadColorBanks.getOrPut(bank) { mutableMapOf<String, Pair<String, List<Int>>>() to null }

        val color = colorBank[this]
        if (color != null) return color.first
        else {
            val last = colorBank[lastColor]
            threadColorBanks[bank] = colorBank to this

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

            colorBank[this] = new
            return new.first
        }
    }

    private fun String.padCTL(length: Int): String {
        var additional = length
        var lastRead = ' '
        var reading = false
        this.forEach {
            if (it == '[' && lastRead == '\u001B') {
                additional += 2
                reading = true
            } else if (reading) {
                if (it == 'm') reading = false
                additional++
            }
            lastRead = it
        }
        return this.padEnd(additional)
    }

    private fun addErrorTraceIterative(
        proxy: ThrowableProxy,
        baseMessage: String, prepend: String = "",
        suppressed: Boolean = false
    ): String {
        var modifiedMessage = baseMessage
        var longestClassLoaderName = 13
        var longestClassName = 0
        var longestLineNumber = 0
        var longestMethodName = 0
        var longestFileName = 0
        var longestModuleName = 0
        var longestModuleVersion = 0
        proxy.stackTrace.forEach { trace ->
            if ((trace.classLoaderName?.length ?: 0) > longestClassLoaderName)
                longestClassLoaderName = trace.classLoaderName?.length ?: 0
            if (trace.className.length > longestClassName) longestClassName = trace.className.length
            if (trace.lineNumber.toString().length > longestLineNumber)
                longestLineNumber = trace.lineNumber.toString().length
            if (trace.methodName.length > longestMethodName) longestMethodName = trace.methodName.length
            trace.fileName?.let { n -> if (n.length > longestFileName) longestFileName = n.length }
            trace.moduleName?.let { n -> if (n.length > longestModuleName) longestModuleName = n.length }
            trace.moduleVersion?.let { n -> if (n.length > longestModuleVersion) longestModuleVersion = n.length }
        }
        val lineLength = longestLineNumber + longestFileName + longestModuleName + longestClassName +
                longestMethodName + longestClassLoaderName + longestModuleVersion + 13
        modifiedMessage += "\n$prepend${if (suppressed) S_EBG else EBG}" + "[${proxy.name}]".padCTL(lineLength) +
                ESC + RESET + END
        (proxy.localizedMessage ?: "<no message>").chunked(lineLength).joinToString("\n") { s ->
            (if (suppressed) S_RBG else RBG) + s.padCTL(lineLength) + ESC + RESET + END
        }.let { f -> modifiedMessage += "\n$prepend$f" }
        modifiedMessage += "\n$prepend$BG$ESC${RED + FOREGROUND}$END.$ESC${WHITE + FOREGROUND}$END "

        val separator = "\n$prepend$BG$ESC${WHITE + FOREGROUND}$END^ "
        modifiedMessage += proxy.stackTrace.joinToString(separator) { trace ->
            '[' + ((trace.fileName?.getColorForString(ColorBanks.FILE) ?: "") + BG).padCTL(longestFileName) +
                    ESC + (WHITE + FOREGROUND) + END +
                    ':' +
                    (if (trace.lineNumber > 0) trace.lineNumber.toString() else "").padCTL(longestLineNumber) +
                    BG +
                    " -> " +
                    ((trace.classLoaderName?.getColorForString(ColorBanks.CLASSLOADER) ?: "System Loader") + BG)
                        .padCTL(longestClassLoaderName) + "] " + BG +
                    (trace.className.getColorForString(ColorBanks.LOGGER) + BG).padCTL(longestClassName) + '.' +
                    ESC + ((if (trace.isNativeMethod) 0 else BRIGHT) + WHITE + FOREGROUND) + END +
                    trace.methodName.padCTL(longestMethodName) + ' ' +
                    ESC + (BRIGHT + CYAN + FOREGROUND) + END +
                    ((trace.moduleName?.getColorForString(ColorBanks.LOGGER) ?: "") + BG).padCTL(longestModuleName) +
                    ' ' + ESC + (CYAN + FOREGROUND) + END +
                    ((trace.moduleVersion ?: "") + BG).padCTL(longestModuleVersion) +
                    ESC + RESET + END
        }
        proxy.causeProxy?.let {
            modifiedMessage = addErrorTraceIterative(
                it,
                modifiedMessage, prepend
            )
        }
        proxy.suppressedProxies.forEach {
            modifiedMessage = addErrorTraceIterative(
                it,
                modifiedMessage, "$prepend$BG x",
                true
            )
        }
        return modifiedMessage
    }

    /**
     * Acts upon a given [LogEvent] for colorization.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun append(event: LogEvent?) {
        if (event != null && event.level.isMoreSpecificThan(Level.INFO)) {
            val formattedTime = LocalDateTime.ofEpochSecond(
                event.instant.epochSecond,
                event.instant.nanoOfSecond,
                ZoneOffset.UTC
            ).format(formatter)

            val prepend = "[${ESC + (FOREGROUND + BRIGHT + BLUE) + END}${formattedTime}${ESC + RESET + END}" +
                    "/${colors[event.level.standardLevel]}${event.level.toString().padEnd(5)}${ESC + RESET + END}] " +
                    "[${event.threadName.getColorForString(ColorBanks.THREAD)}/" +
                    "${event.loggerName.getColorForString(ColorBanks.LOGGER)}]"
            val baseMessage = "$prepend ${event.message.formattedMessage}"

            if (event.thrownProxy == null) {
                DEFAULT_OUT.println(baseMessage)
                return
            }

            DEFAULT_OUT.println(addErrorTraceIterative(event.thrownProxy, baseMessage))
        }
    }
}