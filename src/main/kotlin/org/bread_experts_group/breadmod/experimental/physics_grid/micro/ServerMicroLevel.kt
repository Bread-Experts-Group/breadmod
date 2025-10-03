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
			microLevel.init(mutableMapOf(), mutableMapOf())
			microLevel.setBlockAndUpdate(BlockPos.ZERO, ModBlocks.BREAD_BLOCK.get().block.defaultBlockState())
		}

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
	lateinit var blocks: MutableMap<BlockPos, BlockState>
	lateinit var blockEntities: MutableMap<BlockPos, BlockEntity>

	private fun init(blocks: MutableMap<BlockPos, BlockState>, blockEntities: MutableMap<BlockPos, BlockEntity>) {
		this.logger = LogManager.getLogger()
		this.blocks = blocks
		this.blockEntities = blockEntities
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
		val result = super.clip(context)
		localClient.player!!.displayClientMessage(Component.literal("${result.blockPos}"), true)
		return result
	}
}