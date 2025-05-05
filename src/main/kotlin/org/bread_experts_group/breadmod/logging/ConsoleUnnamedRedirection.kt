package org.bread_experts_group.breadmod.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.PrintStream

internal object ConsoleUnnamedRedirection {
	private val unnamedLoggerOut: Logger = LogManager.getLogger("Unnamed Logger, Standard Out")
	private val unnamedLoggerErr: Logger = LogManager.getLogger("Unnamed Logger, Error")

	class Redirector(val logger: Logger, val level: Level) : PrintStream(nullOutputStream()) {
		private val intermediateBuilder: StringBuilder = StringBuilder()
		override fun print(x: Any?) {
			this.intermediateBuilder.append(x)
			val result = this.intermediateBuilder.toString().split("\n").toMutableList()
			if (result.size > 1) {
				val last = result.removeLast()
				result.forEach(this.logger::warn)
				this.intermediateBuilder.clear()
				this.intermediateBuilder.append(last)
			}
		}

		override fun println(x: Any?): Unit = this.logger.log(this.level, x)
	}

	fun setup() {
		System.setOut(Redirector(this.unnamedLoggerOut, Level.WARN))
		System.setErr(Redirector(this.unnamedLoggerErr, Level.ERROR))
	}
}