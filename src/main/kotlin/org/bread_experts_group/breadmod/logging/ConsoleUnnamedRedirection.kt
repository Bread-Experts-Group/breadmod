package org.bread_experts_group.breadmod.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.PrintStream
import java.util.*

internal object ConsoleUnnamedRedirection {
	private val unnamedLoggerOut : Logger = LogManager.getLogger("Unnamed Logger, Standard Out")
	private val unnamedLoggerIn : Logger = LogManager.getLogger("Unnamed Logger, Standard In")
	private val unnamedLoggerErr : Logger = LogManager.getLogger("Unnamed Logger, Error")

	class Redirector(val logger : Logger, val level : Level) : PrintStream(nullOutputStream()) {
		private val intermediateBuilder = StringBuilder()
		override fun print(x : Any?) {
			this.intermediateBuilder.append(x)
			val result = this.intermediateBuilder.toString().split("\n").toMutableList()
			if (result.size > 1) {
				val last = result.removeLast()
				result.forEach { this.logger.info(it) }
				this.intermediateBuilder.clear()
				this.intermediateBuilder.append(last)
			}
		}

		override fun println(x : Any?) = this.logger.log(this.level, x)
	}

	fun setup() {
		System.setOut(Redirector(this.unnamedLoggerOut, Level.INFO))
		System.setErr(Redirector(this.unnamedLoggerErr, Level.ERROR))
		Thread.ofVirtual().start {
			val sc = Scanner(System.`in`)
			while (true) if (sc.hasNext()) this.unnamedLoggerIn.info(sc.next())
		}
	}
}