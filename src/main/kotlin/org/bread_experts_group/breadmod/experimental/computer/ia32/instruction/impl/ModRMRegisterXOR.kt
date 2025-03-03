package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM

/**
 * Opcode: `31 /r` |
 * Instruction: `XOR r/m(16/32), r(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@IA32Instruction(0x31u)
object ModRMRegisterXOR : Instruction("xor"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = memRM.getRMi() xor register.get().toUInt()
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}
			AddressingLength.R16 -> {
				val result = memRM.getRMs() xor register.get().toUShort()
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}
			else                 -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}