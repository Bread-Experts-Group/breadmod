package org.bread_experts_group.breadmod

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
import org.bread_experts_group.logging.ColoredLogger
import java.time.Instant
import java.util.logging.LogRecord

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

	/**
	 * Acts upon a given [org.apache.logging.log4j.core.LogEvent] for colorization.
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	override fun append(event: LogEvent?) {
		if (event == null) return
		val newRecord = LogRecord(
			when (event.level) {
				Level.INFO  -> java.util.logging.Level.INFO
				Level.WARN  -> java.util.logging.Level.WARNING
				Level.ERROR -> java.util.logging.Level.SEVERE
				Level.FATAL -> java.util.logging.Level.SEVERE
				Level.TRACE -> java.util.logging.Level.FINEST
				Level.DEBUG -> java.util.logging.Level.FINER
				Level.ALL   -> java.util.logging.Level.ALL
				Level.OFF   -> java.util.logging.Level.OFF
				else        -> java.util.logging.Level.INFO
			},
			event.message.formattedMessage
		)
		newRecord.loggerName = event.loggerName ?: event.loggerFqcn
		newRecord.sourceMethodName = event.threadName
		newRecord.sourceClassName = event.loggerFqcn
		newRecord.longThreadID = event.threadId
		newRecord.instant = Instant.ofEpochSecond(event.instant.epochSecond, event.instant.nanoOfSecond.toLong())
		newRecord.thrown = event.thrown
		ColoredLogger.publish(newRecord)
	}
}