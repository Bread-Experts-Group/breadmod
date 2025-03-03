package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.impl

import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister

@IA32InstructionCluster
class LoadFarPointerFromMemoryDefinitions(processor: IA32Processor) {
	class LoadFarPointerWithSegment(segmentN: Char, val register: SegmentRegister) :
		Instruction("l${segmentN}s"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD().let {
			"${it.register}, ${processor.segmentOverride.name}:${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRm, register) = processor.rm()
			this.register.tx = processor.computer.requestMemoryAt16(memRm.memory!! + 4u)
			register.set(processor.computer.requestMemoryAt32(memRm.memory).toULong())
		}

		override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
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