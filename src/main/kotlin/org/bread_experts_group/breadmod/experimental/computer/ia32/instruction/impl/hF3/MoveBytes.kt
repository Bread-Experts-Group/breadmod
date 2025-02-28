package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.ZeroOperandOperatingLengthDependentInstruction
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0xF3A4u)
object MoveBytes : ZeroOperandOperatingLengthDependentInstruction {
	override fun getMnemonic(processor: IA32Processor): String = "rep movs"
	override fun getOperands16(processor: IA32Processor): String =
		"es:[di], ds:[si] -> ds:[si [${hex(processor.si.tx)}] + cx [${hex(processor.c.tx)}]]"

	override fun getOperands32(processor: IA32Processor): String =
		"es:[edi], ds:[esi] -> ds:[esi [${hex(processor.si.tex)}] + ecx [${hex(processor.c.tex)}]]"

	private fun handle(
		processor: IA32Processor,
		c: KMutableProperty0<ULong>, si: KMutableProperty0<ULong>, di: KMutableProperty0<ULong>
	) {
		while (c.get() > 0u) {
			c.set(c.get() - 1u)
			processor.computer.setMemoryAt(
				processor.es.offset(si.get()),
				processor.computer.requestMemoryAt(processor.segOverride.offset(di.get()))
			)
			si.set(si.get() + 1u)
			di.set(di.get() + 1u)
		}
	}

	override fun handle16(processor: IA32Processor): Unit = this.handle(
		processor,
		processor.c::x, processor.si::x, processor.di::x
	)

	override fun handle32(processor: IA32Processor): Unit = this.handle(
		processor,
		processor.c::ex, processor.si::ex, processor.di::ex
	)
}