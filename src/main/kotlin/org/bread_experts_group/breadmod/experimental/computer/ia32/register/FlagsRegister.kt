package org.bread_experts_group.breadmod.experimental.computer.ia32.register

import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor

class FlagsRegister(val processor: IA32Processor, name: String, vararg flags: FlagType) : Register(
	processor.logger,
	name,
	run {
		var sum: ULong = 0u
		flags.forEach { sum = sum or it.position }
		sum
	}
) {
	enum class FlagType(val position: ULong) {
		CARRY_FLAG(0x0000_0000_0000_0001u),
		PARITY_FLAG(0x0000_0000_0000_0004u),
		AUXILIARY_CARRY_FLAG(0x0000_0000_0000_0010u),
		ZERO_FLAG(0x0000_0000_0000_0040u),
		SIGN_FLAG(0x0000_0000_0000_0080u),
		TRAP_FLAG(0x0000_0000_0000_0100u),
		INTERRUPT_ENABLE_FLAG(0x0000_0000_0000_0200u),
		DIRECTION_FLAG(0x0000_0000_0000_0400u),
		OVERFLOW_FLAG(0x0000_0000_0000_0800u),
		IO_PRIVILEGE_LEVEL(0x0000_0000_0000_3000u),
		NESTED_TASK_FLAG(0x0000_0000_0000_4000u),
		RESUME_FLAG(0x0000_0000_0001_0000u),
		VIRTUAL_8086_MODE_FLAG(0x0000_0000_0002_0000u),
		ALIGNMENT_CHECK_ENABLED(0x0000_0000_0004_0000u),
		VIRTUAL_INTERRUPT_FLAG(0x0000_0000_0008_0000u),
		VIRTUAL_INTERRUPT_PENDING(0x0000_0000_0010_0000u),
		CPUID_ALLOWED(0x0000_0000_0020_0000u)
	}

	override var rx: ULong = super.rx
		set(value) {
			if (field == value) return
			field = value
			var flags = ""
			FlagType.entries.forEach {
				flags += if (this.getFlag(it)) ((if (flags.isNotEmpty()) ", " else "") + it.name) else ""
			}
			this.logger.warn("$name set ${hex(value)} [$flags]")
		}

	fun setFlag(flag: FlagType, state: Boolean) {
		var extracted = this.rx and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.rx = extracted
	}

	fun getFlag(flag: FlagType) = (this.rx and flag.position) > 0u

	fun setFlagToResult(flag: FlagType, result: ULong) {
		this.setFlag(flag, this.processor.decoding.getFlagForResult(flag, result))
	}
}