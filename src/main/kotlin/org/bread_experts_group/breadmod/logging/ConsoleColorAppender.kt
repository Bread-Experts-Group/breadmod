package org.bread_experts_group.breadmod.logging

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

typealias ColorBankCount = Pair<String, List<Int>>
typealias ColorBank = MutableMap<String, ColorBankCount>
typealias ColorBankWithLastColor = Pair<ColorBank, String?>

private enum class ColorBanks {
	FILE,
	CLASSLOADER,
	LOGGER,
	THREAD
}

private val colors = mapOf(
	StandardLevel.TRACE to (GraphicsModes.BRIGHT set GraphicsModes.BLACK),
	StandardLevel.DEBUG to mutableListOf(GraphicsModes.WHITE),
	StandardLevel.INFO to (GraphicsModes.BRIGHT set GraphicsModes.GREEN),
	StandardLevel.WARN to (GraphicsModes.BRIGHT set GraphicsModes.YELLOW),
	StandardLevel.ERROR to (GraphicsModes.BRIGHT set GraphicsModes.RED),
	StandardLevel.FATAL to (GraphicsModes.BRIGHT set GraphicsModes.BACKGROUND set GraphicsModes.RED)
)
private val background = GraphicsModes.BLACK set GraphicsModes.BACKGROUND
private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss;SSS")
private val threadColorBanks = mutableMapOf<ColorBanks, ColorBankWithLastColor>()
private val DEFAULT_OUT = PrintStream(FileOutputStream(FileDescriptor.out))

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
	}

	private fun String.getColorForString(bank: ColorBanks): String {
		val (colorBank, lastColor) = threadColorBanks.getOrPut(bank) { mutableMapOf<String, ColorBankCount>() to null }
		val color = colorBank[this]
		if (color != null) return color.first
		val last = colorBank[lastColor]
		threadColorBanks[bank] = colorBank to this
		val new = if (last == null)
			(GraphicsModes.RED join this reset GraphicsModesResets.FG_RESET) to mutableListOf(GraphicsModes.RED.value)
		else {
			val newLast = last.second.toMutableList()
			newLast[0] += 1
			for (index in 0 .. newLast.size) {
				val current = newLast.getOrNull(index) ?: break
				if (current >= GraphicsModes.WHITE.value) {
					newLast[index] = GraphicsModes.RED.value
					if (newLast.size == index + 1) newLast.add(GraphicsModes.RED.value)
					else newLast[index + 1] += 1
				} else break
			}
			var newStr = ""
			for (index in this.indices) {
				val localColor = newLast.getOrNull(index)
				if (localColor != null) {
					newStr += ANSI_CONTROL_SEQUENCE_ESCAPE + localColor + ANSI_GRAPHICS_END + this[index]
				} else {
					newStr += this.slice(index ..< this.length)
					break
				}
			}
			(newStr reset GraphicsModesResets.RESET) to newLast
		}

		colorBank[this] = new
		return new.first
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
		baseMessage: String,
		suppressed: Boolean = false
	): String {
		// Length calculations for padding
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
		val suppressedBanner =
			if (suppressed) mutableListOf(GraphicsModes.WHITE)
			else (GraphicsModes.BRIGHT set GraphicsModes.WHITE)
		// Exception Banner
		modifiedMessage +=
			'\n' + (suppressedBanner join "[${proxy.name}]".padCTL(lineLength) reset GraphicsModesResets.RESET)
		(proxy.localizedMessage ?: "<no message>").chunked(lineLength).joinToString("\n") { s ->
			suppressedBanner join s.padCTL(lineLength) reset GraphicsModesResets.RESET
		}.let { f -> modifiedMessage += "\n$f" }
		// Exception Information
		modifiedMessage += '\n' + (background set GraphicsModes.RED join '.') + (GraphicsModes.WHITE join ' ')
		val separator = '\n' + (background set GraphicsModes.WHITE join "^ ")
		modifiedMessage += proxy.stackTrace.joinToString(separator) { trace ->
			val format = String.format(
				"[%1\$s%2\$s%3\$s → %4\$s] %5\$s.%6\$s %7\$s %8\$s",
				(trace.fileName?.getColorForString(ColorBanks.FILE) ?: "")
					.padCTL(longestFileName),
				GraphicsModes.WHITE join ':',
				(if (trace.lineNumber > 0) trace.lineNumber.toString() else "")
					.padCTL(longestLineNumber),
				(trace.classLoaderName?.getColorForString(ColorBanks.CLASSLOADER) ?: "System Loader")
					.padCTL(longestClassLoaderName),
				trace.className.getColorForString(ColorBanks.LOGGER)
					.padCTL(longestClassName),
				trace.methodName
					.padCTL(longestMethodName),
				(trace.moduleName?.getColorForString(ColorBanks.LOGGER) ?: "")
					.padCTL(longestModuleName),
				(trace.moduleVersion ?: "")
					.padCTL(longestModuleVersion)
			)
			format
		}
		// Suppressed Exceptions
		proxy.causeProxy?.let { modifiedMessage = this.addErrorTraceIterative(it, modifiedMessage) }
		proxy.suppressedProxies.forEach { modifiedMessage = this.addErrorTraceIterative(it, modifiedMessage, true) }
		return modifiedMessage
	}

	/**
	 * Acts upon a given [LogEvent] for colorization.
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	override fun append(event: LogEvent?) {
		if (event != null) {
			val formattedTime = LocalDateTime.ofEpochSecond(
				event.instant.epochSecond,
				event.instant.nanoOfSecond,
				ZoneOffset.UTC
			).format(formatter)
			val level = event.level
			val prepend =
				'[' + (GraphicsModes.BRIGHT join formattedTime reset GraphicsModesResets.RESET) + '/' +
						(colors[level.standardLevel]!! join level.toString()
							.padEnd(5) reset GraphicsModesResets.RESET) +
						"[${event.threadName.getColorForString(ColorBanks.THREAD)}/" +
						"${event.loggerName.getColorForString(ColorBanks.LOGGER)}]"
			val baseMessage = "$prepend ${event.message.formattedMessage}"

			if (event.thrownProxy == null) {
				DEFAULT_OUT.println(baseMessage)
				return
			}

			DEFAULT_OUT.println(this.addErrorTraceIterative(event.thrownProxy, baseMessage))
		}
	}
}