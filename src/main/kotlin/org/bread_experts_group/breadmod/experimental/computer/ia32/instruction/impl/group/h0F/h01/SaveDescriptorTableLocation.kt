package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h0F.h01

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register

/**
 * Saves a descriptor table from the base/limit registers of [baseR] and [limitR] respectively.
 * The instruction mnemonic takes the form of `s[n]dt r/m(16/32)`. |
 * Flags Modified: `none`
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SaveDescriptorTableLocation(
	n: Char, val baseR: Register, val limitR: Register
) : Instruction("s${n}dt"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		"${it.regMem} [${hex(this.baseR.tex)} / ${hex(this.limitR.tx)}]"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		processor.computer.setMemoryAt16(memRM.memory!!, this.limitR.tx)
		processor.computer.setMemoryAt32(memRM.memory + 2u, this.baseR.tex)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}