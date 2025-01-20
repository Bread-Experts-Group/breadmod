package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

/**
 * An "All In One" [BlockEntity].
 * todo actual javadocs.
 */
abstract class BreadModBlockEntity<T : BreadModBlockEntity<T>>(
	type: BlockEntityType<T>,
	pos: BlockPos,
	state: BlockState
) : AbstractTickingBlockEntity<T>(type, pos, state) {
	protected fun updateClients() {
		this@BreadModBlockEntity.level?.sendBlockUpdated(
			this@BreadModBlockEntity.blockPos,
			this@BreadModBlockEntity.blockState,
			this@BreadModBlockEntity.blockState,
			Block.UPDATE_CLIENTS
		)
	}

	override fun getUpdateTag(registries: Provider): CompoundTag {
		val tag = CompoundTag()
		this.saveAdditional(tag, registries)
		return tag
	}

	override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
}