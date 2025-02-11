package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.Processor
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.PrimaryVolume
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot.ElToritoBootRecord
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H09InstructionOR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H31InstructionXOR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H46InstructionINC
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H50InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H56InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H5BInstructionPOP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H68InstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H6AInstructionPUSH
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H74InstructionJE
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H81InstructionADD
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H89InstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8BInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.H8EInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBCInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HBEInstructionMOV
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HC1InstructionSHR
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HE8InstructionCALL
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HEBInstructionJMP
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.HFAInstructionCLI

/**
 * A [Processor] capable of virtualizing the IA-32 architecture.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
class IA32Processor(val computer: Computer) : Processor {
	enum class FlagType(val position: ULong) {
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

	class Register(var rx: ULong) {
		var ex: ULong
			get() = this.rx and 0x00000000FFFFFFFFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFF00000000u) or (value and 0x00000000FFFFFFFFu).toULong()
			}
		var t_ex: UInt
			get() = this.ex.toUInt()
			set(value) {
				this.ex = value.toULong()
			}
		var x: ULong
			get() = this.rx and 0x000000000000FFFFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFF0000u) or (value and 0x000000000000FFFFu).toULong()
			}
		var t_x: UShort
			get() = this.x.toUShort()
			set(value) {
				this.x = value.toULong()
			}
		var l: ULong
			get() = this.rx and 0x00000000000000FFu
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFFFF00u) or (value and 0x00000000000000FFu).toULong()
			}
		var t_l: UByte
			get() = this.l.toUByte()
			set(value) {
				this.l = value.toULong()
			}
		var h: ULong
			get() = (this.rx and 0xFF00u) shr 8
			set(value) {
				this.rx = (this.rx and 0xFFFFFFFFFFFF00FFu) or ((value and 0x000000000000FF00u).toULong() shl 8)
			}
		var t_h: UByte
			get() = this.h.toUByte()
			set(value) {
				this.h = value.toULong()
			}
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
	var cs: Register = Register(0u)
	var ds: Register = Register(0u)
	var ss: Register = Register(0xF000u)
	var se: Register = Register(0u)
	var fs: Register = Register(0u)
	var gs: Register = Register(0u)

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
		this.logger.warn("4# ${hex(value)} -> ${hex(this.ss.t_ex)}:${hex(this.sp.t_ex)}")
		this.computer.setMemoryAt32(((this.ss.t_ex * 0x10u) + this.sp.t_ex).toULong(), value)
	}

	fun push16(value: UShort) {
		this.sp.rx -= 2u
		this.logger.warn("2# ${hex(value)} -> ${hex(this.ss.t_x)}:${hex(this.sp.t_x)}")
		this.computer.setMemoryAt16(((this.ss.t_x * 0x10u) + this.sp.t_x).toULong(), value)
	}

	fun push8(value: UByte) {
		this.sp.rx -= 1u
		this.logger.warn("1# ${hex(value)} -> ${hex(this.ss.t_x)}:${hex(this.sp.t_x)}")
		this.computer.setMemoryAt(((this.ss.t_x * 0x10u) + this.sp.t_x).toULong(), value)
	}

	fun pop16(): UShort {
		val popped = this.computer.requestMemoryAt16(((this.ss.t_x * 0x10u) + this.sp.t_x).toULong())
		this.logger.warn("2# ${hex(this.ss.t_x)}:${hex(this.sp.t_x)} -> ${hex(popped)}")
		this.sp.rx += 2u
		return popped
	}

	fun setFlag(flag: FlagType, state: Boolean) {
		var extracted = this.flags.rx and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.flags.rx = extracted
	}

	fun getFlag(flag: FlagType): Boolean = (this.flags.rx and flag.position) > 0u

	fun setFlagToResult(flag: FlagType, result: ULong) {
		this.setFlag(flag, this.decoding.getFlagForResult(flag, result))
	}

	fun fetch() {
		if (this.ip.rx == (0xFFFFFFF0u).toULong()) {
			val disc = this.computer.disc ?: throw IllegalStateException("Please insert a disc")
			val primary = disc.volumeDescriptors.firstNotNullOf { it as? PrimaryVolume }
			val boot = disc.volumeDescriptors.firstNotNullOf { it as? ElToritoBootRecord }
			val entry = boot.readContents(primary, disc.discStream).standardEntries.first { it.bootable }
			val start = entry.loadSegment * 0x10
			val size = (entry.sectorCount * primary.logicalBlockSize).toULong()
			val discStart = entry.loadRBA.toLong() * primary.logicalBlockSize
			disc.discStream.channel.position(discStart)
			this.logger.warn("BIOS CPY ${hex(discStart)}")
			for (offset in start.toULong() .. start.toULong() + size) {
				// TODO Send in chunks
				this.computer.setMemoryAt(offset, disc.discStream.read().toUByte())
			}
			this.ip.rx = start.toULong()
		}
		this.cir = this.computer.requestMemoryAt(this.ip.rx)
		this.ip.rx++
	}

	var csOverride: Boolean = false
	var bitOverride: Boolean = false
	fun decode() {
		// Useful links when writing decoding:
		// Intel® 64 and IA-32 Architectures: Software Developer’s Manual
		// Volume 2A: Instruction Set Reference, A-L
		// 2.1.5 Table 2-1. 16-Bit Addressing Forms with the ModR/M Byte
		// 2.1.5 Table 2-2. 32-Bit Addressing Forms with the ModR/M Byte
		// 3.1.1.1 Opcode Column in the Instruction Summary Table
		// TODO Exceptions
		this.logger.warn(
			"AT ${hex(this.ip.ex - 1u)}" +
					(if (this.csOverride) ", CS" else "") +
					(if (this.bitOverride) ", 66" else "")
		)
		when (this.cir.toUInt()) {
			0x09u -> H09InstructionOR.handle(this)
			0x2Eu -> {
				this.csOverride = true
				return
			}
			0x31u -> H31InstructionXOR.handle(this)
			0x46u -> H46InstructionINC.handle(this)
			0x50u -> H50InstructionPUSH.handle(this)
			0x56u -> H56InstructionPUSH.handle(this)
			0x5Bu -> H5BInstructionPOP.handle(this)
			0x66u -> {
				this.bitOverride = true
				return
			}
			0x68u -> H68InstructionPUSH.handle(this)
			0x6Au -> H6AInstructionPUSH.handle(this)
			0x74u -> H74InstructionJE.handle(this)
			0x81u -> H81InstructionADD.handle(this)
			0x89u -> H89InstructionMOV.handle(this)
			0x8Bu -> H8BInstructionMOV.handle(this)
			0x8Eu -> H8EInstructionMOV.handle(this)
			0xBCu -> HBCInstructionMOV.handle(this)
			0xBEu -> HBEInstructionMOV.handle(this)
			0xC1u -> HC1InstructionSHR.handle(this)
			0xE8u -> HE8InstructionCALL.handle(this)
			0xEBu -> HEBInstructionJMP.handle(this)
			0xFAu -> HFAInstructionCLI.handle(this)
			else  -> TODO("Unrecognized opcode (${hex(this.cir)})")
		}
		this.csOverride = false
		this.bitOverride = false
	}
}