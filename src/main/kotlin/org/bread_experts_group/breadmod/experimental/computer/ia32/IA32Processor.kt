package org.bread_experts_group.breadmod.experimental.computer.ia32

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity.Companion.processor
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.IA32InstructionCluster
import org.bread_experts_group.breadmod.experimental.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.ControlRegister0
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.Register
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.SegmentRegister
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import kotlin.reflect.KProperty0
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

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
	var cs: SegmentRegister = SegmentRegister(this, "cs", 0xF000u)
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
	var ip: Register = Register(this.logger, "ip", 0xFFF0u)
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

	override fun reset() {
		this.cs.rx = 0xF000u
		this.ip.rx = 0xFFF0u
	}

	val biosHooks: MutableMap<ULong, MutableMap<ULong, (IA32Processor) -> Unit>> = mutableMapOf()
	fun setHook(cs: ULong, ip: ULong, r: (IA32Processor) -> Unit) {
		this.biosHooks.getOrPut(cs) { mutableMapOf() }[ip] = r
	}

	fun initiateInterrupt(selector: UByte) {
		if (this.realMode()) {
			this.logger.warn("!!! INTERRUPT RECEIVED (${hex(selector)}) !!!")
			this.push16(this.flags.tx)
			this.flags.setFlag(FlagType.INTERRUPT_ENABLE_FLAG, false)
			this.flags.setFlag(FlagType.TRAP_FLAG, false)
			this.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, false)
			this.push16(this.cs.tx)
			this.push16(this.ip.tx)
			val addr = selector.toULong() * 4u
			processor.ip.tex = processor.computer.requestMemoryAt16(addr).toUInt()
			processor.cs.tx = processor.computer.requestMemoryAt16(addr + 2u)
		} else {
			throw TODO("Protected mode interrupts")
		}
	}

	fun fetch() {
		if (this.realMode()) this.biosHooks[this.cs.rx]?.get(this.ip.rx)?.invoke(this)
		this.cir = this.computer.requestMemoryAt(this.cs.offset(this.ip))
		this.ip.rx++
	}

	val instructionMap: MutableMap<UInt, Instruction> = mutableMapOf()
	var segmentOverride: SegmentRegister = this.ds
	private var operandSizeOverride: Boolean = false
	private var addressSizeOverride: Boolean = false
	fun realMode(): Boolean = !processor.cr0.getFlag(ControlRegister0.FlagType.PROTECTED_MODE_ENABLE)

	fun getAddressingLengthForSpecifier(specifier: KProperty0<Boolean>): AddressingLength {
		if (!this.realMode()) {
			// Protected Mode
			if (processor.cs.readSegmentDescriptor().flags and 0b0100u > 0u) {
				return if (specifier.get()) AddressingLength.R16
				else AddressingLength.R32
			}
			return if (specifier.get()) AddressingLength.R32
			else AddressingLength.R16
		}
		// Real Mode
		return if (specifier.get()) AddressingLength.R32
		else AddressingLength.R16
	}

	val operandSize: AddressingLength
		get() = this.getAddressingLengthForSpecifier(this::operandSizeOverride)
	val addressSize: AddressingLength
		get() = this.getAddressingLengthForSpecifier(this::addressSizeOverride)

	init {
		val scanner = this::class.java.`package`.getScanner()
		scanner.getClassesAnnotatedWith(IA32Instruction::class).forEach {
			val instructionDescriptor = it.findAnnotation<IA32Instruction>()!!
			require(!this.instructionMap.contains(instructionDescriptor.opcode)) {
				"Multiple opcodes, ${hex(instructionDescriptor.opcode)}"
			}
			this.instructionMap[instructionDescriptor.opcode] =
				(it.objectInstance ?: it.primaryConstructor!!.call(this)) as Instruction
		}
		scanner.getClassesAnnotatedWith(IA32InstructionCluster::class).forEach {
			val cluster = it.primaryConstructor!!.call(this)
			it.declaredMemberProperties.forEach { f ->
				val instructionDescriptor = f.findAnnotation<IA32Instruction>()!!
				require(!this.instructionMap.contains(instructionDescriptor.opcode)) {
					"Multiple opcodes, ${hex(instructionDescriptor.opcode)}"
				}
				this.instructionMap[instructionDescriptor.opcode] = f.getter.call(cluster) as Instruction
			}
		}
		this.logger.warn("Understood ${this.instructionMap.size} opcodes.")
	}

	fun decode() {
		val instruction = when (this.cir.toUInt()) {
			0x26u -> {
				this.segmentOverride = this.es
				return
			}
			0x2Eu -> {
				this.segmentOverride = this.cs
				return
			}
			0x36u -> {
				this.segmentOverride = this.ss
				return
			}
			0x3Eu -> {
				this.segmentOverride = this.ds
				return
			}
			0x64u -> {
				this.segmentOverride = this.fs
				return
			}
			0x65u -> {
				this.segmentOverride = this.gs
				return
			}
			0x66u -> {
				this.operandSizeOverride = true
				return
			}
			0x67u -> {
				this.addressSizeOverride = true
				return
			}
			0x0Fu -> this.instructionMap[(0x0Fu shl 8) or this.decoding.readFetch().toUInt()]
				?: throw IllegalArgumentException("Missing two-byte opcode (0F) for ${hex(this.cir)}")
			0xF3u -> this.instructionMap[(0xF3u shl 8) or this.decoding.readFetch().toUInt()]
				?: throw IllegalArgumentException("Missing two-byte opcode (F3) for ${hex(this.cir)}")
			0xF2u -> this.instructionMap[(0xF2u shl 8) or this.decoding.readFetch().toUInt()]
				?: throw IllegalArgumentException("Missing two-byte opcode (F2) for ${hex(this.cir)}")
			else  -> this.instructionMap[this.cir.toUInt()]
				?: throw IllegalArgumentException("Missing opcode for ${hex(this.cir)}")
		}
		this.logger.warn(
			"{} {}: {}",
			this.cs.hex(this.ip.rx - 1u),
			hex(this.cir),
			instruction.getDisassembly(this)
		)
		instruction.handle(this)
		this.segmentOverride = this.ds
		this.operandSizeOverride = false
		this.addressSizeOverride = false
	}
}