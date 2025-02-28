package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.MemRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMDisassemblyResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.ModRMResult
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.RegisterMemorySingleOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister
import kotlin.reflect.KMutableProperty0

@IA32InstructionCluster
class LoadFarPointerFromMemoryDefinitions(processor: IA32Processor) {
	class LoadFarPointerWithSegment(val segmentN: Char, val register: SegmentRegister) :
		RegisterMemorySingleOperandInstruction {
		override fun getMnemonic(processor: IA32Processor): String = "l${this.segmentN}s"
		override fun getOperands(processor: IA32Processor, rm: ModRMResult, rmD: ModRMDisassemblyResult): String =
			"${rmD.register}, ${processor.segOverride.name}:${rmD.memRM}"

		override fun handle(processor: IA32Processor, rmM: MemRMResult, rmR: KMutableProperty0<ULong>) {
			val addr = rmM.address.get() // TODO, offset, TODO, better overrides
			this.register.tx = processor.computer.requestMemoryAt16(addr + 4u)
			rmR.set(processor.computer.requestMemoryAt32(addr).toULong())
		}

		override val rmRegisterType: RegisterType = RegisterType.GENERAL_PURPOSE
	}

	@IA32Instruction(0xC5u)
	val lds: LoadFarPointerWithSegment = LoadFarPointerWithSegment('d', processor.ds)

	@IA32Instruction(0x0FB2u)
	val lss: LoadFarPointerWithSegment = LoadFarPointerWithSegment('s', processor.ss)

	@IA32Instruction(0xC4u)
	val les: LoadFarPointerWithSegment = LoadFarPointerWithSegment('e', processor.es)

	@IA32Instruction(0x0FB4u)
	val lfs: LoadFarPointerWithSegment = LoadFarPointerWithSegment('f', processor.fs)

	@IA32Instruction(0x0FB5u)
	val lgs: LoadFarPointerWithSegment = LoadFarPointerWithSegment('g', processor.gs)
}