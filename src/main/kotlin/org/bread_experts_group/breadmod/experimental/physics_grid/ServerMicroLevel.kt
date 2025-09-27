package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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
	private val logger: Logger = LogManager.getLogger()
	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.logger.fatal("nuclear bomb")
		return false
	}
}