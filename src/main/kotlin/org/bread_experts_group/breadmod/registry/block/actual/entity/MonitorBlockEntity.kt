package org.bread_experts_group.breadmod.registry.block.actual.entity
//class MonitorBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BreadModBlockEntity<MonitorBlockEntity>(MONITOR.get(), pos, state) {
//	// TODO, real VGA buffer
//	// TODO, move computer to a computer block
//	val logger: Logger = LogManager.getLogger("Bread Computer")
//	var keyboardPos: BlockPos = BlockPos.ZERO
//	val computer: Computer = Computer(
//		listOf(MemoryModule(2097152u)),
//		IA32Processor(),
//		StandardBIOS()
//	)
//
//	@OptIn(ExperimentalUnsignedTypes::class)
//	val computerStepper: Thread = Thread.ofPlatform().unstarted {
//		if (!(this.level ?: return@unstarted).isClientSide) return@unstarted
////		val stream = Assembler(
////			this::class.java.getResource(
////				"/bootloader/fs/boot/loader.asm"
////			)!!.openStream()
////		)
////		this.logger.warn(stream.assemble())
//		this.computer.processor.computer = this.computer
//		this.computer.disc = ISO9660Disc.readDisc(
//			(this::class.java.getResource("/bootloader/bootable.iso") ?: return@unstarted).toURI()
//		)
//		this.computer.reset()
//		try {
//			while (!Thread.currentThread().isInterrupted) this.computer.step()
//		} catch (_: InterruptedException) {
//		}
//	}
//
//	fun isKeyboardBound(): Boolean = this.keyboardPos != BlockPos.ZERO
//
//	fun keyboardStillValid(): Boolean {
//		val level = this.level ?: return false
//		return level.getBlockEntity(this.keyboardPos) != null
//	}
//
//	fun isRunning(): Boolean = this.computerStepper.state != Thread.State.NEW
//
//	fun start(): Unit? = if (!this.isRunning()) this.computerStepper.start() else null
//
//	override fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {
////		this.computer.deserializeNBT(registries, tag.getCompound("computer"))
//		this.keyboardPos = tag.getIntArray("keyboard").toBlockPos()
//	}
//
//	override fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {
//		this.computerStepper.interrupt()
////		tag.put("computer", this.computer.serializeNBT(registries))
//		tag.putIntArray("keyboard", this.keyboardPos.toIntArray())
//	}
//}