package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

/**
 * Opcode: `33 /r` |
 * Instruction: `XOR r(16/32), r/m(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x33u)
object RegisterModRMXOR : Instruction("xor"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = register.get().toUInt() xor memRM.getRMi()
				this.setFlagsForResult(processor, result)
				register.set(result.toULong())
			}
			AddressingLength.R16 -> {
				val result = register.get().toUShort() xor memRM.getRMs()
				this.setFlagsForResult(processor, result)
				register.set(result.toULong())
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}