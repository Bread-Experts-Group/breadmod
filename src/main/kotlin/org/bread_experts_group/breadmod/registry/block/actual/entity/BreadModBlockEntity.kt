package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.util.handlers.ListenerHandler

/**
 * An "All In One" [BlockEntity].
 * todo actual javadocs.
 */
abstract class BreadModBlockEntity<T : BreadModBlockEntity<T>>(
	type: BlockEntityType<T>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(type, pos, state) {
	private fun updateClients() {
		this.setChanged()
		this@BreadModBlockEntity.level?.sendBlockUpdated(
			this@BreadModBlockEntity.blockPos,
			this@BreadModBlockEntity.blockState,
			this@BreadModBlockEntity.blockState,
			Block.UPDATE_CLIENTS
		)
	}

	private var setupState: Boolean = false
	private fun setup() {
		if (this.setupState) return
		this.setupState = true
		val update: ListenerHandler = { _, s, _, _ -> if (!s) this.updateClients(); null }
		if (this is FluidBearingBlockEntity) {
			this.fluidHandler.receiveAction = update
			this.fluidHandler.extractAction = update
		}
		if (this is EnergyBearingBlockEntity) {
			this.energyHandler.receiveAction = update
			this.energyHandler.extractAction = update
		}
	}

	open fun commonTick(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: T
	): Unit = Unit

	open fun clientTick(
		clientLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: T
	): Unit = Unit

	open fun serverTick(
		serverLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: T
	): Unit = Unit

	open fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {}
	open fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {}

	final override fun saveAdditional(tag: CompoundTag, registries: Provider) {
		this.setup()
		super.saveAdditional(tag, registries)
		if (this is ItemBearingBlockEntity) tag.put("bm_be_items", this.itemHandler.serializeNBT(registries))
		if (this is FluidBearingBlockEntity) tag.put("bm_be_fluids", this.fluidHandler.serializeNBT(registries))
		if (this is EnergyBearingBlockEntity) tag.put("bm_be_energy", this.energyHandler.serializeNBT(registries))
		this.saveAdditionalBM(tag, registries)
	}

	final override fun loadAdditional(tag: CompoundTag, registries: Provider) {
		this.setup()
		super.loadAdditional(tag, registries)
		if (this is ItemBearingBlockEntity)
			this.itemHandler.deserializeNBT(registries, tag.getCompound("bm_be_items"))
		if (this is FluidBearingBlockEntity)
			this.fluidHandler.deserializeNBT(registries, tag.getCompound("bm_be_fluids"))
		if (this is EnergyBearingBlockEntity)
			this.energyHandler.deserializeNBT(registries, tag.getCompound("bm_be_energy"))
		this.loadAdditionalBM(tag, registries)
	}

	final override fun getUpdateTag(registries: Provider): CompoundTag =
		CompoundTag().also { this.saveAdditional(it, registries) }

	final override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)
}