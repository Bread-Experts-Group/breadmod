package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction

@IA32Instruction(0xF3A5u)
object MoveWords : Instruction("rep movs") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 ->
			"[esi [${processor.ds.hex(processor.si.ex)}]], es:[edi [${processor.es.hex(processor.d.ex)}]" +
					" + ecx [${hex(processor.c.tex)}]]"
		AddressingLength.R16 ->
			"[si [${processor.ds.hex(processor.si.x)}]], es:[di [${processor.es.hex(processor.d.x)}]" +
					" + cx [${hex(processor.c.tx)}]]"
		else                 -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		when (processor.operandSize) {
			AddressingLength.R32 -> while (processor.c.ex > 0u) {
				processor.c.ex--
				processor.computer.setMemoryAt32(
					processor.es.offset(processor.di),
					processor.computer.requestMemoryAt32(
						processor.ds.offset(processor.si)
					)
				)
				processor.si.ex += 4u
				processor.di.ex += 4u
			}
			AddressingLength.R16 -> while (processor.c.x > 0u) {
				processor.c.x--
				processor.computer.setMemoryAt16(
					processor.es.offset(processor.di),
					processor.computer.requestMemoryAt16(
						processor.ds.offset(processor.si)
					)
				)
				processor.si.x += 2u
				processor.di.x += 2u
			}
			else                 -> throw UnsupportedOperationException()
		}
	}
}