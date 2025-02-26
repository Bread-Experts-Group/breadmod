package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl.group.h0F.h01

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import kotlin.reflect.KMutableProperty0

/**
 * Saves a descriptor table from the base/limit registers of [baseR] and [limitR] respectively.
 * The instruction mnemonic takes the form of `s[n]dt r/m(16/32)`. |
 * Flags Modified: `none`
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SaveDescriptorTableLocation(
	val n: Char,
	val baseR: Register, val limitR: Register
) : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"s${this.n}dt"

	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"${rmD.memRM} [${hex(this.baseR.tex)} / ${hex(this.limitR.tx)}]"

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val addr = rmM.address.get()
		processor.computer.setMemoryAt16(addr, this.limitR.tx)
		processor.computer.setMemoryAt32(addr + 2u, this.baseR.tex)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}