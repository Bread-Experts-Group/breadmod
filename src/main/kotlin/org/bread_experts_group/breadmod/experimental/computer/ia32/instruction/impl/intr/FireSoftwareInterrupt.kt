package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.intr

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Immediate8SingleOperandInstruction
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import kotlin.reflect.full.findAnnotation

@IA32Instruction(0xCDu)
object FireSoftwareInterrupt : Immediate8SingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "int"
	override fun getOperands(processor: IA32Processor, imm8: UByte): String = hex(imm8)
	override fun handle(processor: IA32Processor, imm8: UByte) {
		val errorLabel = "Need BIOS INT for ${hex(imm8)}:${hex(processor.a.th)}"
		val interrupt = (this.biosInterruptMap[imm8] ?: throw IllegalArgumentException(errorLabel))
		val routine = (interrupt[processor.a.th] ?: throw IllegalArgumentException(errorLabel))
		routine.handle(processor)
	}

	val biosInterruptMap: MutableMap<UByte, MutableMap<UByte, BIOSInterruptProvider>> = mutableMapOf()
	val logger: Logger = LogManager.getLogger()

	init {
		val scanner = this::class.java.`package`.getScanner()
		scanner.getClassesAnnotatedWith(BIOSInterrupt::class).forEach {
			val biosDescriptor = it.findAnnotation<BIOSInterrupt>()!!
			this.biosInterruptMap.getOrPut(biosDescriptor.int) { mutableMapOf() }[biosDescriptor.ah] =
				it.objectInstance!! as BIOSInterruptProvider
		}
		this.logger.warn("Understood ${this.biosInterruptMap.size} BIOS interrupts; ${this.biosInterruptMap.entries.sumOf { it.value.size }} routines.")
	}
}