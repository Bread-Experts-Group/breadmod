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
 * Loads a descriptor table into the base/limit registers of [baseR] and [limitR] respectively.
 * The instruction mnemonic takes the form of `l[n]dt r/m(16/32)`. |
 * Flags Modified: `none`
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class LoadDescriptorTableLocation(
	val n: Char,
	val baseR: Register, val limitR: Register
) : RegisterMemorySingleOperandInstruction {
	override fun getMnemonic(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
		"l${this.n}dt"

	override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String {
		val addr = rm.memRM.address.get()
		return "${rmD.memRM} [${hex(processor.computer.requestMemoryAt32(addr + 2u))} / " +
				"${hex(processor.computer.requestMemoryAt16(addr))}]"
	}

	override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
		val addr = rmM.address.get()
		this.limitR.tx = processor.computer.requestMemoryAt16(addr)
		this.baseR.tex = processor.computer.requestMemoryAt32(addr + 2u)
	}

	override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
}