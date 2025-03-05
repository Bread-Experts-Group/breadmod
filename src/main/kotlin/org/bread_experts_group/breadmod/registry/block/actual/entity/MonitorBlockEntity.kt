package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.MemoryModule
import org.bread_experts_group.breadmod.experimental.computer.bios.StandardBIOS
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes.MONITOR
import org.bread_experts_group.breadmod.util.toBlockPos
import org.bread_experts_group.breadmod.util.toIntArray

class MonitorBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<MonitorBlockEntity>(MONITOR.get(), pos, state) {
	// TODO, real VGA buffer
	// TODO, move computer to a computer block
	val logger: Logger = LogManager.getLogger("Bread Computer")
	var keyboardPos: BlockPos = BlockPos.ZERO
	val computer: Computer = Computer(
		listOf(MemoryModule(2097152u)),
		IA32Processor(),
		StandardBIOS
	)
	val computerStepper: Thread = Thread.ofPlatform().unstarted {
		this.computer.processor.computer = this.computer
		this.computer.disc = ISO9660Disc.readDisc(
			this::class.java.getResource(
				"/bootable.iso"
			)!!.toURI()
		)
		this.computer.reset()
		try {
			while (!this.computerStepper.isInterrupted) {
				this.computer.step()
//			Thread.sleep(100)
			}
		} catch (_: InterruptedException) {
		}
	}

	fun isKeyboardBound(): Boolean = this.keyboardPos != BlockPos.ZERO

	fun keyboardStillValid(): Boolean {
		val level = this.level ?: return false
		return level.getBlockEntity(this.keyboardPos) != null
	}

	fun start(): Unit? = if (this.computerStepper.state == Thread.State.NEW) this.computerStepper.start() else null

	override fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {
		this.computer.deserializeNBT(registries, tag.getCompound("computer"))
		this.keyboardPos = tag.getIntArray("keyboard").toBlockPos()
	}

	override fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {
		this.computerStepper.interrupt()
		tag.put("computer", this.computer.serializeNBT(registries))
		tag.putIntArray("keyboard", this.keyboardPos.toIntArray())
	}
}