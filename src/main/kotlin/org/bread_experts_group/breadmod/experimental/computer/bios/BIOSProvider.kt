package org.bread_experts_group.breadmod.experimental.computer.bios

import org.bread_experts_group.breadmod.experimental.computer.Computer

interface BIOSProvider {
	fun initialize(computer: Computer)
}