package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.ZeroOperandInstruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.ControlRegister0
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A [Processor] capable of virtualizing the IA-32 architecture.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
@Suppress("ReplaceNotNullAssertionWithElvisReturn")
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
	val cr0: ControlRegister0 = ControlRegister0(
		this.logger, "cr0",
		ControlRegister0.FlagType.FPU_80387_OR_HIGHER
	)
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
		this.computer.setMemoryAt32(this.ss.offset(this.sp), value)
	}

	fun push16(value: UShort) {
		this.sp.rx -= 2u
		this.computer.setMemoryAt16(this.ss.offset(this.sp), value)
	}

	fun pop32(): UInt {
		val popped = this.computer.requestMemoryAt32(this.ss.offset(this.sp))
		this.sp.rx += 4u
		return popped
	}

	fun pop16(): UShort {
		val popped = this.computer.requestMemoryAt16(this.ss.offset(this.sp))
		this.sp.rx += 2u
		return popped
	}

	fun fetch() {
		if (this.ip.rx == (0xFFFFFFF0u).toULong()) {
			val disc = this.computer.disc ?: return
			val (primary, entry) = disc.getBoot()
			val discStart = (entry.loadRBA.toLong() * primary.logicalBlockSize).toULong()
			val memoryStart = (entry.loadSegment * 0x10).toULong()
			this.decoding.loadDiscIntoMemory(
				discStart,
				discStart + (entry.sectorCount * primary.logicalBlockSize).toULong(),
				memoryStart
			)
			this.d.l = 0xE0u
			this.ip.rx = memoryStart
		}
		this.cir = this.computer.requestMemoryAt(this.cs.offset(this.ip))
		this.ip.rx++
	}

	var csOverride: Boolean = false
	var bitOverride: Boolean = false
	var bit8Override: Boolean = false
	val operatingMode: AddressingLength
		get() = if (this.gdtrBase.tex == 0u || this.cs.tex == 0u) AddressingLength.R16
		else {
			val descriptor = this.cs.readSegmentDescriptor()
			if (descriptor.flags and 0b0010u > 0u) TODO("64-bits mode")
			if (descriptor.flags and 0b0100u > 0u) AddressingLength.R32
			else AddressingLength.R16
		}
	val operatingModeLocal: AddressingLength
		get() = if (this.bit8Override) AddressingLength.R8
		else
			(if (this.bitOverride)
				if (this.operatingMode == AddressingLength.R32) AddressingLength.R16
				else AddressingLength.R32
			else this.operatingMode)
	val instructionMap: MutableMap<UInt, ZeroOperandInstruction> = mutableMapOf()

	init {
		val scanner = this::class.java.`package`.getScanner()
		scanner.getClassesAnnotatedWith(IA32Instruction::class).forEach {
			val instructionDescriptor = it.findAnnotation<IA32Instruction>()!!
			this.instructionMap[instructionDescriptor.opcode] =
				(it.objectInstance ?: it.primaryConstructor!!.call(this)) as ZeroOperandInstruction
		}
		scanner.getClassesAnnotatedWith(IA32InstructionCluster::class).forEach {
			val cluster = it.primaryConstructor!!.call(this)
			it.declaredMemberProperties.forEach { f ->
				val instructionDescriptor = f.findAnnotation<IA32Instruction>()!!
				this.instructionMap[instructionDescriptor.opcode] = f.getter.call(cluster) as ZeroOperandInstruction
			}
		}
		this.logger.warn("Understood ${this.instructionMap.size} opcodes.")
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
			0x2Eu -> {
				this.csOverride = true
				return
			}
			0x66u -> {
				this.bitOverride = true
				return
			}
			0x0Fu -> this.instructionMap[(0x0Fu shl 8) or this.decoding.readFetch().toUInt()]
				?: throw IllegalArgumentException("Missing two-byte opcode for ${hex(this.cir)}")
			else  -> this.instructionMap[this.cir.toUInt()]
				?: throw IllegalArgumentException("Missing opcode for ${hex(this.cir)}")
		}
		this.logger.warn(
			"{} ({},{}): {}",
			this.cs.hex(this.ip.rx - 1u - (if (this.csOverride) 1u else 0u) - (if (this.bitOverride) 1u else 0u)),
			if (this.csOverride) "CS" else "  ",
			if (this.bitOverride) "66" else "  ",
			instruction.getDisassembly(this)
		)
		instruction.handle(this)
		this.csOverride = false
		this.bitOverride = false
		this.bit8Override = false
	}
}