package org.bread_experts_group.breadmod.experimental.computer.ia32

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
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
import java.util.concurrent.CountDownLatch
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
class IA32Processor : Processor {
	override lateinit var computer: Computer
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
		this.a.rx = 0u
		this.b.rx = 0u
		this.c.rx = 0u
		this.d.rx = 0u
		this.sp.rx = 0x6F40u
		this.bp.rx = 0u
		this.di.rx = 0u
		this.si.rx = 0u
		this.cs.rx = 0xF000u
		this.gdtrLimit.rx = 0u
		this.gdtrBase.rx = 0u
		this.idtrLimit.rx = 0u
		this.idtrBase.rx = 0u
		this.ip.rx = 0xFFF0u
		this.ds.rx = 0u
		this.ss.rx = 0u
		this.es.rx = 0u
		this.fs.rx = 0u
		this.gs.rx = 0u
		this.cr0.rx = 0u
		this.cr2.rx = 0u
		this.cr3.rx = 0u
		this.cr4.rx = 0u
		this.flags.rx = 0u
		this.cir = 0u
		this.halt.countDown()
	}

	val biosHooks: MutableMap<ULong, MutableMap<ULong, (IA32Processor) -> Unit>> = mutableMapOf()
	fun setHook(cs: ULong, ip: ULong, r: (IA32Processor) -> Unit) {
		this.biosHooks.getOrPut(cs) { mutableMapOf() }[ip] = r
	}

	var halt: CountDownLatch = CountDownLatch(1)
	fun initiateInterrupt(selector: UByte) {
		this.halt.countDown()
		if (this.realMode()) {
			this.logger.warn("!!! INTERRUPT RECEIVED (${hex(selector)}) !!!")
			this.push16(this.flags.tx)
			this.flags.setFlag(FlagType.INTERRUPT_ENABLE_FLAG, false)
			this.flags.setFlag(FlagType.TRAP_FLAG, false)
			this.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, false)
			this.push16(this.cs.tx)
			this.push16(this.ip.tx)
			val addr = selector.toULong() * 4u
			this.ip.tex = this.computer.requestMemoryAt16(addr).toUInt()
			this.cs.tx = this.computer.requestMemoryAt16(addr + 2u)
		} else {
			throw TODO("Protected mode interrupts")
		}
	}

	fun fetch() {
		this.halt.await()
		if (this.realMode()) this.biosHooks[this.cs.rx]?.get(this.ip.rx)?.invoke(this)
		this.cir = this.computer.requestMemoryAt(this.cs.offset(this.ip))
		this.ip.rx++
	}

	val instructionMap: MutableMap<UInt, Instruction> = mutableMapOf()
	var segment: SegmentRegister = this.ds
	private var operandSizeOverride: Boolean = false
	private var addressSizeOverride: Boolean = false
	fun realMode(): Boolean = !this.cr0.getFlag(ControlRegister0.FlagType.PROTECTED_MODE_ENABLE)

	fun getAddressingLengthForSpecifier(specifier: KProperty0<Boolean>): AddressingLength {
		if (!this.realMode()) {
			// Protected Mode
			if (this.cs.readSegmentDescriptor().flags and 0b0100u > 0u) {
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

	private var readingOffPrefix = 0u
	fun decode() {
		val instruction = when (this.cir.toUInt()) {
			0x26u -> {
				this.segment = this.es
				return
			}
			0x2Eu -> {
				this.segment = this.cs
				return
			}
			0x36u -> {
				this.segment = this.ss
				return
			}
			0x3Eu -> {
				this.segment = this.ds
				return
			}
			0x64u -> {
				this.segment = this.fs
				return
			}
			0x65u -> {
				this.segment = this.gs
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
				?: throw IllegalArgumentException("Missing two-byte opcode (0F) for ${hex(this.cir)} [${hex(this.ip.rx)}]")
			0xF2u -> {
				this.readingOffPrefix = 0xF2u
				return
			}
			0xF3u -> {
				this.readingOffPrefix = 0xF3u
				return
			}
			else  -> {
				if (this.readingOffPrefix > 0u) {
					(this.instructionMap[(this.readingOffPrefix shl 8) or this.cir.toUInt()]
						?: throw IllegalArgumentException(
							"Missing two-byte opcode (${hex(this.readingOffPrefix)}) for ${hex(this.cir)} [${hex(this.ip.rx)}]"
						)).also { this.readingOffPrefix = 0u }
				} else {
					this.instructionMap[this.cir.toUInt()]
						?: throw IllegalArgumentException("Missing opcode for ${hex(this.cir)} [${hex(this.ip.rx)}]")
				}
			}
		}
		this.logger.warn(
			"{} {}: {}",
			this.cs.hex(this.ip.rx - 1u),
			hex(this.cir),
			instruction.getDisassembly(this)
		)
		if (this.ip.rx == (0x7E11u).toULong()) {
			this.logger.warn("A@)")
		}
		instruction.handle(this)
		this.segment = this.ds
		this.operandSizeOverride = false
		this.addressSizeOverride = false
	}

	override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag = CompoundTag().also {
		it.putLong("a", this.a.rx.toLong())
		it.putLong("b", this.c.rx.toLong())
		it.putLong("c", this.b.rx.toLong())
		it.putLong("d", this.d.rx.toLong())
		it.putLong("sp", this.sp.rx.toLong())
		it.putLong("bp", this.bp.rx.toLong())
		it.putLong("di", this.di.rx.toLong())
		it.putLong("si", this.si.rx.toLong())
		it.putLong("cs", this.cs.rx.toLong())
		it.putLong("ds", this.ds.rx.toLong())
		it.putLong("ss", this.ss.rx.toLong())
		it.putLong("es", this.es.rx.toLong())
		it.putLong("fs", this.fs.rx.toLong())
		it.putLong("gs", this.gs.rx.toLong())
		it.putLong("gdtrLimit", this.gdtrLimit.rx.toLong())
		it.putLong("gdtrBase", this.gdtrBase.rx.toLong())
		it.putLong("idtrLimit", this.idtrLimit.rx.toLong())
		it.putLong("idtrBase", this.idtrBase.rx.toLong())
		it.putLong("cr0", this.cr0.rx.toLong())
		it.putLong("cr2", this.cr2.rx.toLong())
		it.putLong("cr3", this.cr3.rx.toLong())
		it.putLong("cr4", this.cr4.rx.toLong())
		it.putLong("flags", this.flags.rx.toLong())
		it.putLong("ip", this.ip.rx.toLong())
		it.putByte("cir", this.cir.toByte())
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, tag: CompoundTag) {
//		this.a.rx = tag.getLong("a").toULong()
//		this.b.rx = tag.getLong("b").toULong()
//		this.c.rx = tag.getLong("c").toULong()
//		this.d.rx = tag.getLong("d").toULong()
//		this.sp.rx = tag.getLong("sp").toULong()
//		this.bp.rx = tag.getLong("bp").toULong()
//		this.di.rx = tag.getLong("di").toULong()
//		this.si.rx = tag.getLong("si").toULong()
//		this.cs.rx = tag.getLong("cs").toULong()
//		this.ds.rx = tag.getLong("ds").toULong()
//		this.ss.rx = tag.getLong("ss").toULong()
//		this.es.rx = tag.getLong("es").toULong()
//		this.fs.rx = tag.getLong("fs").toULong()
//		this.gs.rx = tag.getLong("gs").toULong()
//		this.gdtrLimit.rx = tag.getLong("gdtrLimit").toULong()
//		this.gdtrBase.rx = tag.getLong("gdtrBase").toULong()
//		this.idtrLimit.rx = tag.getLong("idtrLimit").toULong()
//		this.idtrBase.rx = tag.getLong("idtrBase").toULong()
//		this.cr0.rx = tag.getLong("cr0").toULong()
//		this.cr2.rx = tag.getLong("cr2").toULong()
//		this.cr3.rx = tag.getLong("cr3").toULong()
//		this.cr4.rx = tag.getLong("cr4").toULong()
//		this.flags.rx = tag.getLong("flags").toULong()
//		this.ip.rx = tag.getLong("ip").toULong()
//		this.cir = tag.getByte("cir").toUByte()
	}
}