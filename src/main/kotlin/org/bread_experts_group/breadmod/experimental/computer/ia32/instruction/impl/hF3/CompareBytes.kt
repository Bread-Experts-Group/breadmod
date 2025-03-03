package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.BreadMod.Companion.processor
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
		l: (si: ULong, di: ULong) -> ULong,
		c: KMutableProperty0<ULong>, si: KMutableProperty0<ULong>, di: KMutableProperty0<ULong>
	) {
		while (c.get() > 0u) {
			c.set(c.get() - 1u)
			val i = l(processor.es.offset(si.get()), processor.ds.offset(di.get()))
			si.set(si.get() + i)
			di.set(di.get() + i)
			if (!processor.flags.getFlag(FlagType.ZERO_FLAG)) break
		}
	}

	override fun handle(p: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> this.handle(p, { si, di ->
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt32(di),
				processor.computer.requestMemoryAt32(si)
			)
			this.setFlagsForResult(processor, result)
			4u
		}, p.c::ex, p.si::ex, p.di::ex)
		AddressingLength.R16 -> this.handle(p, { si, di ->
			val result = this.setFlagsForOperationR(
				processor,
				processor.computer.requestMemoryAt16(di),
				processor.computer.requestMemoryAt16(si)
			)
			this.setFlagsForResult(processor, result)
			2u
		}, p.c::x, p.si::x, p.di::x)
		else                 -> throw UnsupportedOperationException()
	}
}