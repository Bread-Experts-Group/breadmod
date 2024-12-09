package org.bread_experts_group.breadmod.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.PrintStream
import java.util.*

internal object ConsoleUnnamedRedirection {
    private val unnamedLoggerOut: Logger = LogManager.getLogger("Unnamed Logger, Standard Out")
    private val unnamedLoggerIn: Logger = LogManager.getLogger("Unnamed Logger, Standard In")
    private val unnamedLoggerErr: Logger = LogManager.getLogger("Unnamed Logger, Error")

    class Redirector(val logger: Logger, val level: Level) : PrintStream(nullOutputStream()) {
        private val intermediateBuilder = StringBuilder()

        override fun print(x: Any?) {
            intermediateBuilder.append(x)
            val result = intermediateBuilder.toString().split("\n").toMutableList()
            if (result.size > 1) {
                val last = result.removeLast()
                result.forEach { logger.info(it) }
                intermediateBuilder.clear()
                intermediateBuilder.append(last)
            }
        }

        override fun println(x: Any?) = logger.log(level, x)
    }

    fun setup() {
        System.setOut(Redirector(unnamedLoggerOut, Level.INFO))
        System.setErr(Redirector(unnamedLoggerErr, Level.ERROR))
        Thread.ofVirtual().start {
            val sc = Scanner(System.`in`)
            while (true) unnamedLoggerIn.info(sc.next())
        }
    }
}