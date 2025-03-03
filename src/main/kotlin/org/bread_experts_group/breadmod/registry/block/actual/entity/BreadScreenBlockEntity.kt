package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.Computer
import org.bread_experts_group.breadmod.experimental.computer.MemoryModule
import org.bread_experts_group.breadmod.experimental.computer.bios.StandardBIOS
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes.MONITOR

class BreadScreenBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<BreadScreenBlockEntity>(MONITOR.get(), pos, state) {
	init {
		LogManager.getLogger().warn("new computer being created")
		if (Companion.computerExp.state == Thread.State.NEW) Companion.computerExp.start()
	}

	companion object {
		private val logger = LogManager.getLogger("Bread Computer")
		val newComputer: Computer = Computer()
		val processor: IA32Processor = IA32Processor(this.newComputer)
		val computerExp: Thread = Thread.ofPlatform().unstarted {
			this.newComputer.memory = listOf(MemoryModule(2097152u))
			this.newComputer.processor = this.processor
			this.newComputer.bios = StandardBIOS
			try {
				this.newComputer.disc = ISO9660Disc.readDisc(
					this::class.java.getResource(
						"/MS-DOS 6.22.iso"
					)!!.toURI()
				)
				this.newComputer.reset()
				while (true) {
					this.newComputer.step()
//					Thread.sleep(100)
				}
			} catch (e: Throwable) {
				this.logger.fatal("a: ${hex(this.processor.a.rx)}")
				this.logger.fatal("c: ${hex(this.processor.c.rx)}")
				this.logger.fatal("d: ${hex(this.processor.d.rx)}")
				this.logger.fatal("b: ${hex(this.processor.b.rx)}")
				this.logger.fatal("sp: ${hex(this.processor.sp.rx)}")
				this.logger.fatal("bp: ${hex(this.processor.bp.rx)}")
				this.logger.fatal("si: ${hex(this.processor.si.rx)}")
				this.logger.fatal("di: ${hex(this.processor.di.rx)}")
				this.logger.fatal("ip: ${hex(this.processor.ip.rx)}")
				this.logger.fatal("flags: ${hex(this.processor.flags.tex)}")
				this.logger.fatal("cs: ${hex(this.processor.cs.tx)}")
				this.logger.fatal("ss: ${hex(this.processor.ss.tx)}")
				this.logger.fatal("ds: ${hex(this.processor.ds.tx)}")
				this.logger.fatal("es: ${hex(this.processor.es.tx)}")
				this.logger.fatal("fs: ${hex(this.processor.fs.tx)}")
				this.logger.fatal("gs: ${hex(this.processor.gs.tx)}")
				this.logger.fatal("cr0: ${hex(this.processor.cr0.rx)}")
				this.logger.fatal(e.stackTraceToString())
			}
		}
	}

	// todo sync computer state between server/client
	override fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {
		super.loadAdditionalBM(tag, registries)
	}

	override fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {
		Companion.computerExp.state
	}
}