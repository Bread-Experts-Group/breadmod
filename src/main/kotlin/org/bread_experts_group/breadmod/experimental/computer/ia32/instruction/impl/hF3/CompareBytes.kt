package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import kotlin.reflect.KMutableProperty0

@IA32Instruction(0xF3A6u)
object CompareBytes : Instruction("repe cmps"), ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "ds:[esi], es:[edi [${hex(processor.ds.offset(processor.si.x))}] + ecx [${hex(processor.c.tex)}]]"
		AddressingLength.R16 -> "ds:[si], es:[di [${hex(processor.ds.offset(processor.si.x))}] + cx [${hex(processor.c.tx)}]]"
		else                 -> throw UnsupportedOperationException()
	}

	private fun handle(
		processor: IA32Processor,
		l: (si: ULong, di: ULong) -> Unit,
		c: KMutableProperty0<ULong>, si: KMutableProperty0<ULong>, di: KMutableProperty0<ULong>
	) {
		while (c.get() > 0u) {
			c.set(c.get() - 1u)
			l(processor.es.offset(di.get()), processor.ds.offset(si.get()))
			si.set(si.get() + 1u)
			di.set(di.get() + 1u)
			if (!processor.flags.getFlag(FlagType.ZERO_FLAG)) break
		}
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> this.handle(processor, { si, di ->
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt(di),
				processor.computer.requestMemoryAt(si)
			)
			this.setFlagsForResult(processor, result)
		}, processor.c::ex, processor.si::ex, processor.di::ex)
		AddressingLength.R16 -> this.handle(processor, { si, di ->
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt(di),
				processor.computer.requestMemoryAt(si)
			)
			this.setFlagsForResult(processor, result)
		}, processor.c::x, processor.si::x, processor.di::x)
		else                 -> throw UnsupportedOperationException()
	}
}