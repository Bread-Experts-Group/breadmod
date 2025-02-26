package org.bread_experts_group.breadmod.experimental.computer.ia32.instruction

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.readBinary
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.experimental.computer.ia32.register.FlagsRegister.FlagType
import java.util.Optional
import kotlin.reflect.KMutableProperty0

class DecodingUtil(private val processor: IA32Processor) {
	enum class AddressingLength {
		R8,
		R16,
		R32
	}

	enum class RegisterType {
		GENERAL_PURPOSE,
		SEGMENT,
		MM,
		XMM,
		CONTROL_REGISTER,
		DEBUG_REGISTER
	}

	fun getRegRM(reg: UInt, type: RegisterType): KMutableProperty0<ULong> {
		val register = when (reg) {
			0b000u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.a
				RegisterType.SEGMENT          -> return this.processor.es::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr0::ex
				else                          -> TODO("Add $type")
			}
			0b001u -> when (type) {
				RegisterType.GENERAL_PURPOSE -> this.processor.c
				RegisterType.SEGMENT         -> return this.processor.cs::x
				else                         -> TODO("Add $type")
			}
			0b010u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.d
				RegisterType.SEGMENT          -> return this.processor.ss::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr2::ex
				else                          -> TODO("Add $type")
			}
			0b011u -> when (type) {
				RegisterType.GENERAL_PURPOSE  -> this.processor.b
				RegisterType.SEGMENT          -> return this.processor.ds::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr3::ex
				else                          -> TODO("Add $type")
			}
			0b100u -> when (type) {
				RegisterType.GENERAL_PURPOSE  ->
					if (this.processor.operatingModeLocal == AddressingLength.R8) {
						return this.processor.a::h
					} else this.processor.sp
				RegisterType.SEGMENT          -> return this.processor.fs::x
				RegisterType.CONTROL_REGISTER -> return this.processor.cr4::ex
				else                          -> TODO("Add $type")
			}
			0b101u -> when (type) {
				RegisterType.GENERAL_PURPOSE ->
					if (this.processor.operatingModeLocal == AddressingLength.R8) {
						return this.processor.c::h
					} else this.processor.bp
				RegisterType.SEGMENT         -> return this.processor.gs::x
				else                         -> TODO("Add $type")
			}
			0b110u -> when (type) {
				RegisterType.GENERAL_PURPOSE ->
					if (this.processor.operatingModeLocal == AddressingLength.R8) {
						return this.processor.d::h
					} else this.processor.si
				else                         -> TODO("Add $type")
			}
			0b111u -> when (type) {
				RegisterType.GENERAL_PURPOSE ->
					if (this.processor.operatingModeLocal == AddressingLength.R8) {
						return this.processor.b::h
					} else this.processor.di
				else                         -> TODO("Add $type")
			}
			else   -> throw IllegalStateException(hex(reg))
		}
		return when (this.processor.operatingModeLocal) {
			AddressingLength.R8  -> register::l
			AddressingLength.R16 -> register::x
			AddressingLength.R32 -> register::ex
		}
	}

	fun getRegRMDisassembler(reg: UInt, type: RegisterType): String = when (reg) {
		0b000u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "al [${hex(this.processor.a.tl)}]"
				AddressingLength.R16 -> "ax [${hex(this.processor.a.tx)}]"
				AddressingLength.R32 -> "eax [${hex(this.processor.a.tex)}]"
			}
			RegisterType.SEGMENT          -> "es [${hex(this.processor.es.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr0 [${hex(this.processor.cr0.tex)}]"
			else                          -> TODO("Add $type")
		}
		0b001u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "cl [${hex(this.processor.c.tl)}]"
				AddressingLength.R16 -> "cx [${hex(this.processor.c.tx)}]"
				AddressingLength.R32 -> "ecx [${hex(this.processor.c.tex)}]"
			}
			RegisterType.SEGMENT         -> "cs [${hex(this.processor.cs.tx)}]"
			else                         -> TODO("Add $type")
		}
		0b010u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "dl [${hex(this.processor.d.tl)}]"
				AddressingLength.R16 -> "dx [${hex(this.processor.d.tx)}]"
				AddressingLength.R32 -> "edx [${hex(this.processor.d.tex)}]"
			}
			RegisterType.SEGMENT          -> "ss [${hex(this.processor.ss.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr2 [${hex(this.processor.cr2.tex)}]"
			else                          -> TODO("Add $type")
		}
		0b011u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "bl [${hex(this.processor.b.tl)}]"
				AddressingLength.R16 -> "bx [${hex(this.processor.b.tx)}]"
				AddressingLength.R32 -> "ebx [${hex(this.processor.b.tex)}]"
			}
			RegisterType.SEGMENT          -> "ds [${hex(this.processor.ds.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr3 [${hex(this.processor.cr3.tex)}]"
			else                          -> TODO("Add $type")
		}
		0b100u -> when (type) {
			RegisterType.GENERAL_PURPOSE  -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "ah [${hex(this.processor.a.th)}]"
				AddressingLength.R16 -> "sp [${hex(this.processor.sp.tx)}]"
				AddressingLength.R32 -> "esp [${hex(this.processor.sp.tex)}]"
			}
			RegisterType.SEGMENT          -> "fs [${hex(this.processor.fs.tx)}]"
			RegisterType.CONTROL_REGISTER -> "cr4 [${hex(this.processor.cr4.tex)}]"
			else                          -> TODO("Add $type")
		}
		0b101u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "ch [${hex(this.processor.c.th)}]"
				AddressingLength.R16 -> "bp [${hex(this.processor.bp.tx)}]"
				AddressingLength.R32 -> "ebp [${hex(this.processor.bp.tex)}]"
			}
			RegisterType.SEGMENT         -> "gs [${hex(this.processor.gs.tx)}]"
			else                         -> TODO("Add $type")
		}
		0b110u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "dh [${hex(this.processor.d.th)}]"
				AddressingLength.R16 -> "si [${hex(this.processor.si.tx)}]"
				AddressingLength.R32 -> "esi [${hex(this.processor.si.tex)}]"
			}
			else                         -> TODO("Add $type")
		}
		0b111u -> when (type) {
			RegisterType.GENERAL_PURPOSE -> when (this.processor.operatingModeLocal) {
				AddressingLength.R8  -> "bh [${hex(this.processor.b.th)}]"
				AddressingLength.R16 -> "di [${hex(this.processor.di.tx)}]"
				AddressingLength.R32 -> "edi [${hex(this.processor.di.tex)}]"
			}
			else                         -> TODO("Add $type")
		}
		else   -> throw IllegalStateException(hex(reg))
	}

	inner class MemRMResult(
		val register: Optional<KMutableProperty0<ULong>>,
		val address: Optional<ULong>
	) {
		fun <T> decide(ifRegister: (KMutableProperty0<ULong>) -> T, ifAddress: (ULong) -> T): T =
			if (this.register.isPresent) ifRegister(this.register.get())
			else ifAddress(this.address.get())

		fun getValue(): ULong =
			if (this.register.isPresent) this.register.get().get()
			else {
				val addr =
					(if (this@DecodingUtil.processor.csOverride) this@DecodingUtil.processor.cs else this@DecodingUtil.processor.ds).offset(
						this.address.get()
					)
				when (this@DecodingUtil.processor.operatingModeLocal) {
					AddressingLength.R32 -> this@DecodingUtil.processor.computer.requestMemoryAt32(addr).toULong()
					AddressingLength.R16 -> this@DecodingUtil.processor.computer.requestMemoryAt16(addr).toULong()
					AddressingLength.R8  -> this@DecodingUtil.processor.computer.requestMemoryAt(addr).toULong()
				}
			}

		fun setValue(v: ULong): Unit =
			if (this.register.isPresent) this.register.get().set(v)
			else {
				val addr =
					(if (this@DecodingUtil.processor.csOverride) this@DecodingUtil.processor.cs
					else this@DecodingUtil.processor.ds).offset(this.address.get())
				when (this@DecodingUtil.processor.operatingModeLocal) {
					AddressingLength.R32 -> this@DecodingUtil.processor.computer.setMemoryAt32(addr, v.toUInt())
					AddressingLength.R16 -> this@DecodingUtil.processor.computer.setMemoryAt16(addr, v.toUShort())
					AddressingLength.R8  -> this@DecodingUtil.processor.computer.setMemoryAt(addr, v.toUByte())
				}
			}
	}

	fun readBinaryFetch(length: Int): Long = readBinary(length, {
		this.processor.fetch()
		this.processor.cir
	})

	fun readFetch(): UByte = this.processor.fetch().let { this.processor.cir }
	fun readBinaryForMode(): Comparable<*> = when (this.processor.operatingModeLocal) {
		AddressingLength.R32 -> this.readBinaryFetch(4).toUInt()
		AddressingLength.R16 -> this.readBinaryFetch(2).toUShort()
		AddressingLength.R8  -> this.readBinaryFetch(1).toUByte()
	}

	fun decodeSIB(): ULong {
		this.processor.fetch()
		val (scale, index, base) = this.getComponents(this.processor.cir)
		return (when (index) {
			0b000u -> this.processor.a.ex
			0b001u -> this.processor.c.ex
			0b010u -> this.processor.d.ex
			0b011u -> this.processor.b.ex
			0b100u -> this.processor.sp.ex
			0b110u -> this.processor.si.ex
			0b111u -> this.processor.di.ex
			else   -> throw IllegalArgumentException("SIB index $index")
		} * when (scale) {
			0b00u -> 1u
			0b01u -> 2u
			0b10u -> 4u
			0b11u -> 8u
			else  -> throw IllegalArgumentException("SIB scale $scale")
		}) + when (base) {
			0b000u -> this.processor.a.ex
			0b001u -> this.processor.c.ex
			0b010u -> this.processor.d.ex
			0b011u -> this.processor.b.ex
			0b100u -> 0u
			0b101u -> this.processor.bp.ex
			0b110u -> this.processor.si.ex
			0b111u -> this.processor.di.ex
			else   -> throw IllegalArgumentException("SIB base $base")
		}
	}

	fun decodeSIBDisassembler(): String {
		this.processor.fetch()
		val (scale, index, base) = this.getComponents(this.processor.cir)
		return '(' + when (index) {
			0b000u -> "eax"
			0b001u -> "ecx"
			0b010u -> "edx"
			0b011u -> "ebx"
			0b100u -> "esp"
			0b110u -> "esi"
			0b111u -> "edi"
			else   -> throw IllegalArgumentException("SIB index $index")
		} + when (scale) {
			0b00u -> ""
			0b01u -> "*2"
			0b10u -> "*4"
			0b11u -> "*8"
			else  -> throw IllegalArgumentException("SIB scale $scale")
		} + ')' + when (base) {
			0b000u -> "+eax"
			0b001u -> "+ecx"
			0b010u -> "+edx"
			0b011u -> "+ebx"
			0b100u -> ""
			0b101u -> "+ebp"
			0b110u -> "+esi"
			0b111u -> "+edi"
			else   -> throw IllegalArgumentException("SIB base $base")
		}
	}

	fun getMemRM(mod: UInt, rm: UInt, regRMType: RegisterType): MemRMResult {
		val pair = when (mod) {
			0b00u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.a.ex
						else this.processor.b.x + this.processor.si.x
					0b001u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.c.ex
						else this.processor.b.x + this.processor.di.x
					0b010u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.d.ex
						else this.processor.bp.x + this.processor.si.x
					0b011u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.b.ex
						else this.processor.bp.x + this.processor.di.x
					0b100u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.decodeSIB()
						else this.processor.si.x
					0b101u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.readBinaryFetch(4).toULong()
						else this.processor.di.x
					0b110u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.si.ex
						else this.readBinaryFetch(2).toULong()
					0b111u ->
						if (this.processor.operatingMode == AddressingLength.R32) this.processor.di.ex
						else this.processor.b.x
					else   -> throw IllegalStateException("Mod 00, RM ${hex(rm)}")
				}
			)
			0b01u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u -> TODO("[BX+SI]+disp8")
					0b001u -> TODO("[BX+DI]+disp8")
					0b010u -> TODO("[BP+SI]+disp8")
					0b011u -> TODO("[BP+DI]+disp8")
					0b100u ->
						if (this.processor.operatingMode == AddressingLength.R32)
							(this.decodeSIB().toInt() + this.readBinaryFetch(1).toByte()).toULong()
						else (this.processor.si.tx.toShort() + this.readBinaryFetch(1).toByte()).toULong()
					0b101u ->
						if (this.processor.operatingMode == AddressingLength.R32)
							(this.processor.bp.tex.toInt() + this.readBinaryFetch(1).toByte()).toULong()
						else (this.processor.di.tx.toShort() + this.readBinaryFetch(1).toByte()).toULong()
					0b110u -> TODO("[BP]+disp8")
					0b111u -> TODO("[BX]+disp8")
					else   -> throw IllegalStateException("Mod 01, RM ${hex(rm)}")
				}
			)
			0b10u -> Optional.empty<KMutableProperty0<ULong>>() to Optional.of(
				when (rm) {
					0b000u -> TODO("[BX+SI]+disp16")
					0b001u -> TODO("[BX+DI]+disp16")
					0b010u ->
						if (this.processor.operatingMode == AddressingLength.R32)
							(this.processor.d.tex.toInt() + this.readBinaryFetch(4).toInt()).toULong()
						else ((this.processor.bp.tx.toShort() + this.processor.si.tx.toShort()) + this.readBinaryFetch(2)
							.toShort()).toULong()
					0b011u -> TODO("[BP+DI]+disp16")
					0b100u -> TODO("[SI]+disp16") // [sib+disp32], [si]+disp16
					0b101u ->
						if (this.processor.operatingMode == AddressingLength.R32)
							(this.processor.bp.tex.toInt() + this.readBinaryFetch(4).toInt()).toULong()
						else (this.processor.di.tx.toShort() + this.readBinaryFetch(2).toShort()).toULong()
					0b110u -> TODO("[BP]+disp16")
					0b111u ->
						if (this.processor.operatingMode == AddressingLength.R32)
							(this.processor.di.tex.toInt() + this.readBinaryFetch(4).toInt()).toULong()
						else (this.processor.b.tx.toShort() + this.readBinaryFetch(2).toShort()).toULong()
					else   -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
				}
			)
			0b11u -> Optional.of(this.getRegRM(rm, regRMType)) to Optional.empty()
			else  -> throw IllegalStateException(hex(mod))
		}
		return MemRMResult(pair.first, pair.second)
	}

	fun getMemRMDisassembler(
		mod: UInt,
		rm: UInt,
		regRMType: RegisterType
	): String = when (mod) {
		0b00u ->
			'[' + when (rm) {
				0b000u ->
					if (this.processor.operatingMode == AddressingLength.R32) "eax [${hex(this.processor.a.tex)}]"
					else "bx+si [${hex(this.processor.b.tx + this.processor.si.tx)}]"
				0b001u ->
					if (this.processor.operatingMode == AddressingLength.R32) "ecx [${hex(this.processor.c.tex)}]"
					else "bx+di [${hex(this.processor.b.x + this.processor.di.x)}]"
				0b010u ->
					if (this.processor.operatingMode == AddressingLength.R32) "edx [${hex(this.processor.d.tex)}]"
					else "bp+si [${hex(this.processor.bp.x + this.processor.si.x)}]"
				0b011u ->
					if (this.processor.operatingMode == AddressingLength.R32) "ebx [${hex(this.processor.b.tex)}]"
					else "bp+di [${this.processor.bp.x + this.processor.di.x}]"
				0b100u ->
					if (this.processor.operatingMode == AddressingLength.R32) this.decodeSIBDisassembler()
					else "si [${hex(this.processor.si.tx)}]"
				0b101u ->
					if (this.processor.operatingMode == AddressingLength.R32) hex(this.readBinaryFetch(4).toInt())
					else "di [${hex(this.processor.di.tx)}]"
				0b110u ->
					if (this.processor.operatingMode == AddressingLength.R32) "esi [${hex(this.processor.si.tex)}]"
					else hex(this.readBinaryFetch(2).toShort())
				0b111u ->
					if (this.processor.operatingMode == AddressingLength.R32) "edi [${hex(this.processor.di.tex)}]"
					else "bx"
				else   -> throw IllegalStateException("Mod 00, RM ${hex(rm)}")
			} + ']'
		0b01u ->
			'[' + when (rm) {
				0b000u -> TODO("[BX+SI]+disp8")
				0b001u -> TODO("[BX+DI]+disp8")
				0b010u -> TODO("[BP+SI]+disp8")
				0b011u -> TODO("[BP+DI]+disp8")
				0b100u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						hex(this.decodeSIB().toInt() + this.readBinaryFetch(1).toByte())
					else this.readBinaryFetch(1).toByte()
						.let { "si+${hex(it)} [${hex(this.processor.si.tx.toShort() + it)}]" }
				0b101u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						this.readBinaryFetch(1).toByte()
							.let { "ebp+${hex(it)} [${hex(this.processor.bp.tex.toInt() + it)}]" }
					else this.readBinaryFetch(1).toByte()
						.let { "di+${hex(it)} [${hex(this.processor.di.tx.toShort() + it)}]" }
				0b110u -> TODO("[BP]+disp8")
				0b111u -> TODO("[BX]+disp8")
				else   -> throw IllegalStateException("Mod 01, RM ${hex(rm)}")
			} + ']'
		0b10u ->
			'[' + when (rm) {
				0b000u -> TODO("[BX+SI]+disp16")
				0b001u -> TODO("[BX+DI]+disp16")
				0b010u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						this.readBinaryFetch(4).toInt()
							.let { "edx+${hex(it)} [${hex(this.processor.d.tex.toInt() + it)}]" }
					else
						this.readBinaryFetch(2).toShort().let {
							"bp+si+${hex(it)} [${hex(this.processor.bp.tx.toShort() + this.processor.si.tx.toShort() + it)}]"
						}
				0b011u -> TODO("[BP+DI]+disp16")
				0b100u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						this.readBinaryFetch(4)
							.toUInt().let {
								"${this.decodeSIBDisassembler().also { this.processor.ip.rx-- }}+" +
										"${hex(it)} [${hex(this.decodeSIB() + it)}]"
							}
					else
						this.readBinaryFetch(2).toShort().let {
							"si+${hex(it)} [${hex(this.processor.si.tx.toShort() + it)}]"
						}
				0b101u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						this.readBinaryFetch(4).toInt()
							.let { "ebp+${hex(it)} [${hex(this.processor.bp.tex.toInt() + it)}]" }
					else
						this.readBinaryFetch(2).toShort()
							.let { "di+${hex(it)} [${hex(this.processor.di.tx.toShort() + it)}]" }
				0b110u -> TODO("[BP]+disp16")
				0b111u ->
					if (this.processor.operatingMode == AddressingLength.R32)
						this.readBinaryFetch(4).toInt()
							.let { "edi+${hex(it)} [${hex(this.processor.di.tex.toInt() + it)}]" }
					else
						this.readBinaryFetch(2).toShort()
							.let { "bx+${hex(it)} [${hex(this.processor.b.tx.toShort() + it)}]" }
				else   -> throw IllegalStateException("Mod 10, RM ${hex(rm)}")
			} + ']'
		0b11u -> this.getRegRMDisassembler(rm, regRMType)
		else  -> throw IllegalStateException(hex(mod))
	}

	data class ModRMResult(
		val memRM: MemRMResult,
		val register: KMutableProperty0<ULong>
	)

	data class ModRMDisassemblyResult(
		val memRM: String,
		val register: String
	)

	fun getComponents(modRm: UByte): Triple<UInt, UInt, UInt> {
		val mod = modRm.toUInt() shr 6
		val reg = (modRm.toUInt() shr 3) and 0b111u
		val rm = modRm.toUInt() and 0b111u
		return Triple(mod, reg, rm)
	}

	fun getModRM(modRm: UByte, registerType: RegisterType): ModRMResult {
		val (mod, reg, rm) = this.getComponents(modRm)
		return ModRMResult(
			this.getMemRM(mod, rm, RegisterType.GENERAL_PURPOSE),
			this.getRegRM(reg, registerType)
		)
	}

	fun getModRMDisassembler(modRm: UByte, registerType: RegisterType): ModRMDisassemblyResult {
		val (mod, reg, rm) = this.getComponents(modRm)
		return ModRMDisassemblyResult(
			this.getMemRMDisassembler(mod, rm, RegisterType.GENERAL_PURPOSE),
			this.getRegRMDisassembler(reg, registerType)
		)
	}

	fun getFlagForResult(flag: FlagType, value: ULong): Boolean = when (flag) {
		FlagType.SIGN_FLAG   -> value.toLong() < 0
		FlagType.ZERO_FLAG   -> value == ULong.MIN_VALUE
		FlagType.PARITY_FLAG -> value.toUByte().countOneBits() % 2 == 0
		else                 -> TODO("Unsupported flag: $flag")
	}

	fun loadDiscIntoMemory(start: ULong, end: ULong, memoryStart: ULong) {
		val disc = this.processor.computer.disc!!
		disc.discStream.channel.position(start.toLong())
		this.processor.logger.warn("BIOS CPY ${hex(start)} -> ${hex(end)} @ ${hex(memoryStart)}")
		for (offset in memoryStart .. memoryStart + (end - start)) {
			// TODO Send in chunks
			this.processor.computer.setMemoryAt(offset, disc.discStream.read().toUByte())
		}
	}
}