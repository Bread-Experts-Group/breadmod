package org.bread_experts_group.breadmod.registry.block.handler

import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.Read.FloppyGeometry.Companion.floppy5_14_320K
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class ComputerHandler : ParentedHandler<BreadModBlockEntity> {
	companion object {
		val BLOCK_VOID: BlockCapability<ComputerHandler, Void?> = BlockCapability.createVoid<ComputerHandler>(
			modLocation("computer"),
			ComputerHandler::class.java
		)
	}

	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	override lateinit var parent: BreadModBlockEntity
	val computer: Computer = Computer(
		listOf(MemoryModule(2097152u)),
		IA32Processor(),
		StandardBIOS()
	)
	val computerStepper: Thread = Thread.ofPlatform().unstarted {
//		val stream = Assembler(
//			this::class.java.getResource(
//				"/bootloader/fs/boot/loader.asm"
//			)!!.openStream()
//		)
//		this.logger.warn(stream.assemble())
		this.computer.processor.computer = this.computer
		val floppyDataPath = Files.createTempFile("flp", "img" + System.currentTimeMillis())
		Files.newOutputStream(floppyDataPath).use {
			(this::class.java.getResourceAsStream("/CDPDOS125.IMG") ?: return@unstarted).use { imgData ->
				imgData.transferTo(it)
			}
		}
		val floppyData = Files.newByteChannel(floppyDataPath, StandardOpenOption.READ, StandardOpenOption.WRITE)
		this.computer.floppies[0] = floppyData to floppy5_14_320K
		this.computer.reset()
		try {
			while (!Thread.currentThread().isInterrupted) {
				this.computer.step()
				this.stateUpdated()
			}
		} catch (_: InterruptedException) {
		}
	}
	var running: Boolean = this.computerStepper.state != Thread.State.NEW
		private set

	fun start() {
		if (!this.running) this.computerStepper.start() else null
	}

	fun reset() {
		this.computer.reset()
	}
}