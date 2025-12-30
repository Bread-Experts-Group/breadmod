package org.bread_experts_group.breadmod_agent

import java.lang.instrument.Instrumentation

@Suppress("Unused")
class Agent {
	companion object {
		@JvmStatic
		fun premain(agentArgs: String?, instrumentation: Instrumentation) {
			println("THE BREADMOD AGENT HAS INFECTED THE GAME")
		}
	}
}