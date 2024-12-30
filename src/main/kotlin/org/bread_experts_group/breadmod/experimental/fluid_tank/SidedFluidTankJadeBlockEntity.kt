package org.bread_experts_group.breadmod.experimental.fluid_tank

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTank.CustomHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class SidedFluidTankJadeBlockEntity(
	pos : BlockPos,
	state : BlockState
) : BlockEntity(ModBlockEntityTypes.FLUID_TANK_JADE_ENTITY.get(), pos, state) {
	inner class SyncedHandler(capacity : Int, canFill : Boolean, canDrain : Boolean) :
		SidedFluidTank.CustomHandler(capacity, canFill, canDrain) {
		override fun onContentsChanged() {
			this@SidedFluidTankJadeBlockEntity.level?.sendBlockUpdated(
				this@SidedFluidTankJadeBlockEntity.blockPos,
				this@SidedFluidTankJadeBlockEntity.blockState,
				this@SidedFluidTankJadeBlockEntity.blockState,
				Block.UPDATE_ALL
			)
		}
	}

	val tank : SidedFluidTank = SidedFluidTank(
		listOf(
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true)
		).map { this.SyncedHandler(it.first, it.second, it.third) }
	)

	val sides : MutableMap<Direction, CustomHandler?> =
		mutableMapOf(
			Direction.UP to this.tank.tanks[0],
			Direction.DOWN to this.tank.tanks[1],
			Direction.EAST to null,
			Direction.WEST to null,
			Direction.NORTH to null,
			Direction.SOUTH to null
		)

	override fun saveAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.saveAdditional(tag, registries)
		this.tank.writeToNBT(registries, tag)
	}

	override fun loadAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.loadAdditional(tag, registries)
		this.tank.readFromNBT(registries, tag)
	}
	//    fun syncToClients() = level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
	override fun getUpdateTag(registries : HolderLookup.Provider) : CompoundTag =
		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }

	override fun getUpdatePacket() : Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)
}