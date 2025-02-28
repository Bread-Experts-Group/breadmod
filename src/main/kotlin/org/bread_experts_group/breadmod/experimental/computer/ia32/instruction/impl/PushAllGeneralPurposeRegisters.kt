package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction

@IA32Instruction(0x60u)
object PushAllGeneralPurposeRegisters : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "pusha"
	override fun getOperands16(processor: IA32Processor): String = ""
	override fun getOperands32(processor: IA32Processor): String = ""
	override fun handle16(processor: IA32Processor) {
		val saved = processor.sp.tx
		processor.push16(processor.a.tx)
		processor.push16(processor.c.tx)
		processor.push16(processor.d.tx)
		processor.push16(processor.b.tx)
		processor.push16(saved)
		processor.push16(processor.bp.tx)
		processor.push16(processor.si.tx)
		processor.push16(processor.di.tx)
	}

	override fun handle32(processor: IA32Processor) {
		val saved = processor.sp.tex
		processor.push32(processor.a.tex)
		processor.push32(processor.c.tex)
		processor.push32(processor.d.tex)
		processor.push32(processor.b.tex)
		processor.push32(saved)
		processor.push32(processor.bp.tex)
		processor.push32(processor.si.tex)
		processor.push32(processor.di.tex)
	}
}