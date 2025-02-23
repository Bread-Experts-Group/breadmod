package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H00InstructionADD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H01InstructionADD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H09InstructionOR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H0F01InstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H0F20InstructionMOVCR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H0F22InstructionMOVCR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H28InstructionSUB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H29InstructionSUB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H31InstructionXOR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H39InstructionCMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H3CInstructionCMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H46InstructionINC
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H50InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H51InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H52InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H53InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H56InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H59InstructionPOP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H5BInstructionPOP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H68InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H6AInstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H72InstructionJB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H73InstructionJAE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H74InstructionJE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H75InstructionJNZ
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H76InstructionJBE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H81InstructionADD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H83InstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H84InstructionTEST
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H88InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H89InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8AInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8BInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8EInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HA1InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HA3InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HACInstructionLODSB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HB4InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HB8InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HB9InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBBInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBCInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBDInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBEInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HC1InstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HC3InstructionRET
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HCDInstructionINT
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HD1InstructionSHR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HE6InstructionOUT
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HE8InstructionCALL
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HEAInstructionLJMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HEBInstructionJMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HF6InstructionTEST
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFAInstructionCLI
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFBInstructionSTI
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFCInstructionCLD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFEInstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.ControlRegister0
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister
import kotlin.reflect.jvm.jvmName

/**
 * A [Processor] capable of virtualizing the IA-32 architecture.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
class IA32Processor(val computer: Computer) : Processor {
	override fun step() {
		this.fetch()
		this.decode()
	}

	val decoding: DecodingUtil = DecodingUtil(this)
	val logger: Logger = LogManager.getLogger("Processor")

	// General Purpose
	val a: Register = Register(this.logger, "a", 0u)
	var b: Register = Register(this.logger, "b", 0u)
	var c: Register = Register(this.logger, "c", 0u)
	var d: Register = Register(this.logger, "d", 0u)
	var sp: Register = Register(this.logger, "sp", 0x6F40u)
	var bp: Register = Register(this.logger, "bp", 0u)

	// Source/Dest
	var di: Register = Register(this.logger, "di", 0u)
	var si: Register = Register(this.logger, "si", 0u)

	// Segment
	var cs: SegmentRegister = SegmentRegister(this, "cs", 0u)
	var ds: SegmentRegister = SegmentRegister(this, "ds", 0u)
	var ss: SegmentRegister = SegmentRegister(this, "ss", 0u)
	var es: SegmentRegister = SegmentRegister(this, "es", 0u)
	var fs: SegmentRegister = SegmentRegister(this, "fs", 0u)
	var gs: SegmentRegister = SegmentRegister(this, "gs", 0u)

	// Global Descriptor Table
	var gdtrLimit: Register = Register(this.logger, "gdtrLimit", 0u)
	var gdtrBase: Register = Register(this.logger, "gdtrBase", 0u)

	// Interrupt Descriptor Table
	var idtrLimit: Register = Register(this.logger, "idtrLimit", 0u)
	var idtrBase: Register = Register(this.logger, "idtrBase", 0u)

	// Control
	val cr0: ControlRegister0 = ControlRegister0(this.logger, "cr0")
	val cr2: Register = Register(this.logger, "cr2", 0u)
	val cr3: Register = Register(this.logger, "cr3", 0u)
	val cr4: Register = Register(this.logger, "cr4", 0u)

	// State
	var flags: FlagsRegister = FlagsRegister(this, "flags")

	/**
	 * The current instruction pointer of this [IA32Processor].
	 *
	 * The initial value
	 * (otherwise known as the [Reset Vector](https://en.wikipedia.org/wiki/Reset_vector))
	 * is at physical address `0xFFFFFFF0`, which is the BIOS entry point.
	 * For ease of implementation, the BIOS is not present in ROMs on Bread Mod computers;
	 * instead, a Kotlin-written BIOS will be run, which will then do the boot loading process.
	 * @see fetch
	 * @since 1.0.0
	 * @author Miko Elbrecht
	 */
	var ip: Register = Register(this.logger, "ip", 0xFFFFFFF0u)
	var cir: UByte = 0u

	fun push32(value: UInt) {
		this.sp.rx -= 4u
		this.computer.setMemoryAt32(ss.offset(sp), value)
	}

	fun push16(value: UShort) {
		this.sp.rx -= 2u
		this.computer.setMemoryAt16(ss.offset(sp), value)
	}

	fun pop32(): UInt {
		val popped = this.computer.requestMemoryAt32(ss.offset(sp))
		this.sp.rx += 4u
		return popped
	}

	fun pop16(): UShort {
		val popped = this.computer.requestMemoryAt16(ss.offset(sp))
		this.sp.rx += 2u
		return popped
	}

	fun fetch() {
		if (this.ip.rx == (0xFFFFFFF0u).toULong()) {
			val disc = this.computer.disc!!
			val (primary, entry) = disc.getBoot()
			val start = entry.loadSegment * 0x10
			val size = (entry.sectorCount * primary.logicalBlockSize).toULong()
			val discStart = entry.loadRBA.toLong() * primary.logicalBlockSize
			disc.discStream.channel.position(discStart)

			this.logger.warn("BIOS CPY ${hex(discStart)} -> ${hex(discStart.toULong() + size)} @ ${hex(start)}")
			for (offset in start.toULong() .. start.toULong() + size) {
				// TODO Send in chunks
				this.computer.setMemoryAt(offset, disc.discStream.read().toUByte())
			}
			this.d.l = 0xE0u
			this.ip.rx = start.toULong()
		}
		this.cir = this.computer.requestMemoryAt(this.cs.offset(this.ip))
		this.ip.rx++
	}

	var csOverride: Boolean = false
	var bitOverride: Boolean = false
	var bit8Override: Boolean = false
	val operatingMode: DecodingUtil.AddressingLength
		get() = if (this.cr0.getFlag(ControlRegister0.FlagType.PROTECTED_MODE_ENABLE)) DecodingUtil.AddressingLength.R32
		else DecodingUtil.AddressingLength.R16
	val operatingModeLocal: DecodingUtil.AddressingLength
		get() = if (bit8Override) DecodingUtil.AddressingLength.R8
		else
			(if (bitOverride)
				if (operatingMode == DecodingUtil.AddressingLength.R32) DecodingUtil.AddressingLength.R16
				else DecodingUtil.AddressingLength.R32
			else operatingMode)

	fun decode() {
		// Useful links when writing decoding:
		// Intel® 64 and IA-32 Architectures: Software Developer’s Manual
		// Volume 2A: Instruction Set Reference, A-L
		// 2.1.5 Table 2-1. 16-Bit Addressing Forms with the ModR/M Byte
		// 2.1.5 Table 2-2. 32-Bit Addressing Forms with the ModR/M Byte
		// 3.1.1.1 Opcode Column in the Instruction Summary Table
		// TODO Exceptions
		val instruction = when (this.cir.toUInt()) {
			0x00u -> H00InstructionADD
			0x01u -> H01InstructionADD
			0x09u -> H09InstructionOR
			0x0Fu -> {
				this.fetch()
				when (this.cir.toUInt()) {
					0x01u -> H0F01InstructionGROUP
					0x20u -> H0F20InstructionMOVCR
					0x22u -> H0F22InstructionMOVCR
					else  -> TODO("Unrecognized 2-byte opcode (${hex(this.cir)})")
				}
			}
			0x28u -> H28InstructionSUB
			0x29u -> H29InstructionSUB
			0x2Eu -> {
				this.csOverride = true
				return
			}
			0x31u -> H31InstructionXOR
			0x39u -> H39InstructionCMP
			0x3Cu -> H3CInstructionCMP
			0x46u -> H46InstructionINC
			0x50u -> H50InstructionPUSH
			0x51u -> H51InstructionPUSH
			0x52u -> H52InstructionPUSH
			0x53u -> H53InstructionPUSH
			0x56u -> H56InstructionPUSH
			0x59u -> H59InstructionPOP
			0x5Bu -> H5BInstructionPOP
			0x66u -> {
				this.bitOverride = true
				return
			}
			0x68u -> H68InstructionPUSH
			0x6Au -> H6AInstructionPUSH
			0x72u -> H72InstructionJB
			0x73u -> H73InstructionJAE
			0x74u -> H74InstructionJE
			0x75u -> H75InstructionJNZ
			0x76u -> H76InstructionJBE
			0x81u -> H81InstructionADD
			0x83u -> H83InstructionGROUP
			0x84u -> H84InstructionTEST
			0x88u -> H88InstructionMOV
			0x89u -> H89InstructionMOV
			0x8Au -> H8AInstructionMOV
			0x8Bu -> H8BInstructionMOV
			0x8Eu -> H8EInstructionMOV
			0xA1u -> HA1InstructionMOV
			0xA3u -> HA3InstructionMOV
			0xACu -> HACInstructionLODSB
			0xB4u -> HB4InstructionMOV
			0xB8u -> HB8InstructionMOV
			0xB9u -> HB9InstructionMOV
			0xBBu -> HBBInstructionMOV
			0xBCu -> HBCInstructionMOV
			0xBDu -> HBDInstructionMOV
			0xBEu -> HBEInstructionMOV
			0xC1u -> HC1InstructionGROUP
			0xC3u -> HC3InstructionRET
			0xCDu -> HCDInstructionINT
			0xD1u -> HD1InstructionSHR
			0xE6u -> HE6InstructionOUT
			0xE8u -> HE8InstructionCALL
			0xEAu -> HEAInstructionLJMP
			0xEBu -> HEBInstructionJMP
			0xF6u -> HF6InstructionTEST
			0xFAu -> HFAInstructionCLI
			0xFBu -> HFBInstructionSTI
			0xFCu -> HFCInstructionCLD
			0xFEu -> HFEInstructionGROUP
			else  -> TODO("Unrecognized opcode (${hex(this.cir)})")
		}
		this.logger.warn(
			"{} ({},{}): {}",
			this.cs.hex(this.ip.rx - 1u),
			if (this.csOverride) "CS" else "  ",
			if (this.bitOverride) "66" else "  ",
			instruction.getDisassembly(this)
		)
		if (this.csOverride && !instruction.supportsCodeSegmentOverride)
			throw UnsupportedOperationException("${instruction::class.jvmName} does not support CS")
		instruction.prepare(this)
		(if (operatingModeLocal == DecodingUtil.AddressingLength.R32) instruction::handle32
		else instruction::handle16)(this)
		this.csOverride = false
		this.bitOverride = false
	}
}