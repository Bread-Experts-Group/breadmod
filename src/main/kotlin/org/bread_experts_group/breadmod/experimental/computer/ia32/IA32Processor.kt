package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil
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
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H29InstructionSUB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H31InstructionXOR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H39InstructionCMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H3CInstructionCMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H46InstructionINC
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H50InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H51InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H52InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H56InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H59InstructionPOP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H5BInstructionPOP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H68InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H6AInstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H72InstructionJB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H73InstructionJAE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H74InstructionJE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H75InstructionJNEoJNZ
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H76InstructionJBE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H81InstructionADD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H83InstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H88InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H89InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8BInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8EInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HA1InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HA3InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HACInstructionLODSB
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HB4InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HB8InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBBInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBCInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBDInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBEInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HC1InstructionGROUP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HC3InstructionRET
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HCDInstructionINT
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HD1InstructionSHR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HE8InstructionCALL
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HEAInstructionLJMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HEBInstructionJMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HF6InstructionTEST
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFAInstructionCLI
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFBInstructionSTI
import kotlin.reflect.jvm.jvmName

/**
 * A [Processor] capable of virtualizing the IA-32 architecture.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
class IA32Processor(val computer: Computer) : Processor {
	enum class FLAGSFlagType(val position: ULong) {
		CARRY_FLAG(0x0000_0001u),
		PARITY_FLAG(0x0000_0004u),
		AUXILIARY_CARRY_FLAG(0x0000_0010u),
		ZERO_FLAG(0x0000_0040u),
		SIGN_FLAG(0x0000_0080u),
		TRAP_FLAG(0x0000_0100u),
		INTERRUPT_ENABLE_FLAG(0x0000_0200u),
		DIRECTION_FLAG(0x0000_0400u),
		OVERFLOW_FLAG(0x0000_0800u),
		IO_PRIVILEDGE_LEVEL(0x0000_3000u),
		NESTED_TASK_FLAG(0x0000_4000u),
		RESUME_FLAG(0x0001_0000u),
		VIRTUAL_8086_MODE_FLAG(0x0002_0000u),
		ALIGNMENT_CHECK_ENABLED(0x0004_0000u),
		VIRTUAL_INTERRUPT_FLAG(0x0008_0000u),
		VIRTUAL_INTERRUPT_PENDING(0x0010_0000u),
		CPUID_ALLOWED(0x0020_0000u)
	}

	open class Register(var rx: ULong) {
		var ex: ULong
			get() = this.rx and 0x00000000FFFFFFFFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFF00000000u) or (value and 0x00000000FFFFFFFFu)
			}
		var tex: UInt
			get() = this.ex.toUInt()
			set(value) {
				this.ex = value.toULong()
			}
		var x: ULong
			get() = this.rx and 0x000000000000FFFFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFF0000u) or (value and 0x000000000000FFFFu)
			}
		var tx: UShort
			get() = this.x.toUShort()
			set(value) {
				this.x = value.toULong()
			}
		var l: ULong
			get() = this.rx and 0x00000000000000FFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFFFF00u) or (value and 0x00000000000000FFu)
			}
		var tl: UByte
			get() = this.l.toUByte()
			set(value) {
				this.l = value.toULong()
			}
		var h: ULong
			get() = (this.rx and 0xFF00u) shr 8
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFF00FFu) or ((value and 0x00000000000000FFu) shl 8)
			}
		var th: UByte
			get() = this.h.toUByte()
			set(value) {
				this.h = value.toULong()
			}
	}

	class SegmentRegister(rx: ULong) : Register(rx) {
		fun offset(o: ULong): ULong = (this.rx * 0x10u) + o
		fun offset(r: Register): ULong = this.offset(r.rx)
		fun hex(o: ULong): String = "${BinaryUtil.hex(this.rx)}:${BinaryUtil.hex(o).substring(2)}"
		fun hex(r: Register): String = this.hex(r.rx)
	}

	enum class CR0FlagType(val position: ULong) {
		PROTECTED_MODE_ENABLE(0x0000_0001u)
	}

	class ControlRegister0(vararg flags: CR0FlagType) : Register(
		run {
			var sum: ULong = 0u
			flags.forEach { sum = sum or it.position }
			sum
		}
	) {
		fun setFlag(flag: CR0FlagType, state: Boolean) {
			var extracted = this.rx and (flag.position.inv())
			if (state) extracted = extracted or flag.position
			this.rx = extracted
		}

		fun getFlag(flag: CR0FlagType) = (this.rx and flag.position) > 0u
	}

	override fun step() {
		this.fetch()
		this.decode()
	}

	val decoding: DecodingUtil = DecodingUtil(this)

	// General Purpose
	val a: Register = Register(0u)
	var b: Register = Register(0u)
	var c: Register = Register(0u)
	var d: Register = Register(0u)
	var sp: Register = Register(0xFFFEu)
	var bp: Register = Register(0u)

	// Source/Dest
	var di: Register = Register(0u)
	var si: Register = Register(0u)

	// Segment
	var cs: SegmentRegister = SegmentRegister(0u)
	var ds: SegmentRegister = SegmentRegister(0u)
	var ss: SegmentRegister = SegmentRegister(0xF000u)
	var es: SegmentRegister = SegmentRegister(0u)
	var fs: SegmentRegister = SegmentRegister(0u)
	var gs: SegmentRegister = SegmentRegister(0u)

	// Global Descriptor Table
	var gdtrLimit: Register = Register(0u)
	var gdtrBase: Register = Register(0u)

	// Control
	val cr0: ControlRegister0 = ControlRegister0()
	val cr2: Register = Register(0u)
	val cr3: Register = Register(0u)
	val cr4: Register = Register(0u)

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
	var ip: Register = Register(0xFFFFFFF0u)
	var flags: Register = Register(0u)
	var cir: UByte = 0u
	val logger: Logger = LogManager.getLogger()

	fun push32(value: UInt) {
		this.sp.rx -= 4u
		this.logger.warn("4# PUSH ${hex(value)} -> ${this.ss.hex(this.sp)}")
		this.computer.setMemoryAt32(this.ss.offset(this.sp), value)
	}

	fun push16(value: UShort) {
		this.sp.rx -= 2u
		this.logger.warn("2# PUSH ${hex(value)} -> ${this.ss.hex(this.sp)}")
		this.computer.setMemoryAt16(this.ss.offset(this.sp), value)
	}

	fun pop32(): UInt {
		val popped = this.computer.requestMemoryAt32(this.ss.offset(this.sp))
		this.sp.rx += 4u
		this.logger.warn("4# POP ${this.ss.hex(this.sp)} -> ${hex(popped)}")
		return popped
	}

	fun pop16(): UShort {
		val popped = this.computer.requestMemoryAt16(this.ss.offset(this.sp))
		this.sp.rx += 2u
		this.logger.warn("2# POP ${this.ss.hex(this.sp)} -> ${hex(popped)}")
		return popped
	}

	fun setFlag(flag: FLAGSFlagType, state: Boolean) {
		var extracted = this.flags.rx and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.flags.rx = extracted
	}

	fun getFlag(flag: FLAGSFlagType): Boolean = (this.flags.rx and flag.position) > 0u

	fun setFlagToResult(flag: FLAGSFlagType, result: ULong) {
		this.setFlag(flag, this.decoding.getFlagForResult(flag, result))
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
	fun operatingMode(noOverride: Boolean = false): DecodingUtil.AddressingLength =
		if (this.cr0.getFlag(CR0FlagType.PROTECTED_MODE_ENABLE)) {
			if (!noOverride && this.bitOverride) DecodingUtil.AddressingLength.R16
			else DecodingUtil.AddressingLength.R32
		} else {
			if (!noOverride && this.bitOverride) DecodingUtil.AddressingLength.R32
			else DecodingUtil.AddressingLength.R16
		}

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
			0x75u -> H75InstructionJNEoJNZ
			0x76u -> H76InstructionJBE
			0x81u -> H81InstructionADD
			0x83u -> H83InstructionGROUP
			0x88u -> H88InstructionMOV
			0x89u -> H89InstructionMOV
			0x8Bu -> H8BInstructionMOV
			0x8Eu -> H8EInstructionMOV
			0xA1u -> HA1InstructionMOV
			0xA3u -> HA3InstructionMOV
			0xACu -> HACInstructionLODSB
			0xB4u -> HB4InstructionMOV
			0xB8u -> HB8InstructionMOV
			0xBBu -> HBBInstructionMOV
			0xBCu -> HBCInstructionMOV
			0xBDu -> HBDInstructionMOV
			0xBEu -> HBEInstructionMOV
			0xC1u -> HC1InstructionGROUP
			0xC3u -> HC3InstructionRET
			0xCDu -> HCDInstructionINT
			0xD1u -> HD1InstructionSHR
			0xE8u -> HE8InstructionCALL
			0xEAu -> HEAInstructionLJMP
			0xEBu -> HEBInstructionJMP
			0xF6u -> HF6InstructionTEST
			0xFAu -> HFAInstructionCLI
			0xFBu -> HFBInstructionSTI
			else  -> TODO("Unrecognized opcode (${hex(this.cir)})")
		}
		this.logger.warn(
			"{} ({},{}): {}",
			this.cs.hex(this.ip.rx - 1u),
			if (this.csOverride) "CS" else "  ",
			if (this.bitOverride) "66" else "  ",
			instruction::class.simpleName
		)
		if (this.csOverride && !instruction.supportsCodeSegmentOverride)
			throw UnsupportedOperationException("${instruction::class.jvmName} does not support CS")
		instruction.prepare(this)
		(if (this.operatingMode() == DecodingUtil.AddressingLength.R32) instruction::handle32
		else instruction::handle16)(this)
		this.csOverride = false
		this.bitOverride = false
	}
}