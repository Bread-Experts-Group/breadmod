package org.bread_experts_group.breadmod.experimental.physics_grid.micro

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.toVec3
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
		fun create(
			blocks: MutableMap<BlockPos, BlockState>,
			blockEntities: MutableMap<BlockPos, BlockEntity>
		): ServerMicroLevel {
			val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
			theUnsafe.isAccessible = true
			val unsafe = theUnsafe.get(null) as Unsafe
			val microLevel = unsafe.allocateInstance(ServerMicroLevel::class.java) as ServerMicroLevel
			microLevel.init(blocks, blockEntities)
			return microLevel
		}
	}

	private lateinit var logger: Logger
	private lateinit var grid: PhysicsGrid
	lateinit var blocks: MutableMap<BlockPos, BlockState>
	lateinit var blockEntities: MutableMap<BlockPos, BlockEntity>

	private fun init(
		blocks: MutableMap<BlockPos, BlockState>,
		blockEntities: MutableMap<BlockPos, BlockEntity>
	) {
		this.logger = LogManager.getLogger()
		this.blocks = blocks
		this.blockEntities = blockEntities
	}

	fun initGrid(grid: PhysicsGrid) {
		this.grid = grid
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.blocks[pos] = state
		this.logger.fatal("nuclear bomb")
		return false
	}

	override fun getBlockState(pos: BlockPos): BlockState =
		this.blocks[pos] ?: Blocks.AIR.defaultBlockState()

	override fun getBlockEntity(pos: BlockPos): BlockEntity? =
		this.blockEntities[pos]

	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState()

	override fun clip(context: ClipContext): BlockHitResult {
		val initial = super.clip(context)
		val result = BlockHitResult(
			initial.location - this.grid.pos,
			initial.direction,
			BlockPos.containing(initial.blockPos.toVec3() - this.grid.pos),
			initial.isInside
		)
		localClient.player!!.displayClientMessage(Component.literal("${result.blockPos}, ${result.location}"), true)
		return result
	}
}