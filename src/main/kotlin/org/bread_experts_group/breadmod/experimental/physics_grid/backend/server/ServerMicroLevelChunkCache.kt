package org.bread_experts_group.breadmod.experimental.physics_grid.backend.server

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelChunkMap
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateBlockUpdatePhysicsGridPacket
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toVec3i
import java.util.function.BooleanSupplier

class ServerMicroLevelChunkCache(
	private val parent: ServerMicroLevel
) : ServerChunkCache(
	null, null, null, null,
	null, null, 0, 0,
	false, null, null, null
) {
	init {
		this.chunkMap = MicroLevelChunkMap(this.parent, this)
	}

	val singletonChunk: ServerMicroLevelChunk = ServerMicroLevelChunk(this.parent)
	override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess {
		return this.singletonChunk
	}

	override fun tick(hasTimeLeft: BooleanSupplier, tickChunks: Boolean) {
		if (this.parent.tickRateManager().runsNormally()) {
			val holder = ServerChunkCache.ChunkAndHolder(
				this.singletonChunk,
				(this.chunkMap as MicroLevelChunkMap).singletonHolder
			)

			this.parent.tickChunk(holder.chunk, this.parent.gameRules.getInt(GameRules.RULE_RANDOMTICKING))
			holder.holder.broadcastChanges(holder.chunk)
		}
	}

	override fun blockChanged(pos: BlockPos) {
		/*
        TODO: this.broadcastBlockEntityIfNeeded(list1, level, blockpos, blockstate);
		 */
		PacketDistributor.sendToPlayersTrackingChunk(
			this.parent.sourceLevel,
			ChunkPos(
				BlockPos(this.parent.grid.pos.add(pos.toVec3()).toVec3i())
			),
			EncapsulateBlockUpdatePhysicsGridPacket(
				this.parent.grid.id,
				ClientboundBlockUpdatePacket(pos, this.parent.getBlockState(pos))
			)
		)
	}

	override fun hasChunk(x: Int, z: Int): Boolean = true
}