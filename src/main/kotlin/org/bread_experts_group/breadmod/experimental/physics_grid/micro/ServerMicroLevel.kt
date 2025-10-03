package org.bread_experts_group.breadmod.experimental.physics_grid.micro

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import sun.misc.Unsafe

class ServerMicroLevel : ServerLevel(
	MicroMinecraftServer(),
	null,
	null,
	null,
	null,
	null,
	null,
	false,
	0,
	null,
	true,
	null
) {
	companion object {
		fun testLevel() {
			// TODO: The worst Bread Mod code ever written
			// Incredibly powerful and dangerous, needs an alternative ASAP
			val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
			theUnsafe.isAccessible = true
			val unsafe = theUnsafe.get(null) as Unsafe
			val microLevel = unsafe.allocateInstance(ServerMicroLevel::class.java) as ServerMicroLevel
			microLevel.initLogger()
			microLevel.setBlockAndUpdate(BlockPos.ZERO, ModBlocks.BREAD_BLOCK.get().block.defaultBlockState())
		}
	}

	private lateinit var logger: Logger

	fun initLogger() {
		this.logger = LogManager.getLogger()
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.logger.fatal("nuclear bomb")
		return false
	}
}