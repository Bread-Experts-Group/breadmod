package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.util.handlers.ExtendedItemStackHandler
import org.bread_experts_group.breadmod.util.handlers.SidedFluidTank

/**
 * An "All In One" [BlockEntity].
 * todo actual javadocs.
 */
abstract class BreadModBlockEntity<T : BreadModBlockEntity<T>>(
	type : BlockEntityType<T>,
	pos : BlockPos,
	state : BlockState,
	private val itemSlots : Int = 0,
	private val energyCapacity : Int = 0,
	private val fluidTanks : List<Triple<Int, Boolean, Boolean>> = listOf()
) : AbstractTickingBlockEntity<T>(type, pos, state) {
	inner class SyncedFluidHandler(capacity : Int, canFill : Boolean, canDrain : Boolean) :
		SidedFluidTank.CustomHandler(capacity, canFill, canDrain) {
		override fun onContentsChanged() {
			this@BreadModBlockEntity.setChanged()
			this@BreadModBlockEntity.updateClients()
		}
	}

	inner class SyncedEnergy(capacity : Int) : EnergyStorage(capacity) {
		override fun receiveEnergy(toReceive : Int, simulate : Boolean) : Int {
			this@BreadModBlockEntity.setChanged()
			this@BreadModBlockEntity.updateClients()
			return super.receiveEnergy(toReceive, simulate)
		}

		override fun extractEnergy(toExtract : Int, simulate : Boolean) : Int {
			this@BreadModBlockEntity.setChanged()
			this@BreadModBlockEntity.updateClients()
			return super.extractEnergy(toExtract, simulate)
		}
	}

	val items : ExtendedItemStackHandler = ExtendedItemStackHandler(this.itemSlots)
	val energy : SyncedEnergy = this.SyncedEnergy(this.energyCapacity)
	val tank : SidedFluidTank = SidedFluidTank(
		this.fluidTanks.map { this.SyncedFluidHandler(it.first, it.second, it.third) }
	)
	var energyDivision : Int? = null
	fun dropContents() {
		this.items.let { item ->
			val list = NonNullList.createWithCapacity<ItemStack>(item.slots)
			repeat(item.slots) {
				list.add(item.getStackInSlot(it))
				this.items.setStackInSlot(it, ItemStack.EMPTY)
			}
			this.level?.let { Containers.dropContents(it, this.blockPos, list) }
		}
	}

	fun getFluid(tank : Int) : FluidStack = this.tank.getFluidInTank(tank)
	fun setFluid(tank : Int, stack : FluidStack) {
		this.tank.tanks[tank].fluid = stack
	}

	fun getItem(slot : Int) : ItemStack = this.items.getStackInSlot(slot)
	fun setItem(slot : Int, stack : ItemStack) : Boolean =
		if (this.items.emptySlots() > 0) {
			this.items.setStackInSlot(slot, stack)
			true
		} else false

	fun growItem(slot : Int, count : Int) {
		this.getItem(slot).grow(count)
	}

	@Suppress("unused")
	fun shrinkItem(slot : Int, count : Int) {
		this.getItem(slot).shrink(count)
	}

	fun growFluid(tank : Int, amount : Int) {
		this.getFluid(tank).grow(amount)
	}

	@Suppress("unused")
	fun shrinkFluid(tank : Int, amount : Int) {
		this.getFluid(tank).shrink(amount)
	}

	private fun updateClients() =
		this@BreadModBlockEntity.level?.sendBlockUpdated(
			this@BreadModBlockEntity.blockPos,
			this@BreadModBlockEntity.blockState,
			this@BreadModBlockEntity.blockState,
			Block.UPDATE_CLIENTS
		)

	override fun saveAdditional(tag : CompoundTag, registries : Provider) {
		super.saveAdditional(tag, registries)
		this.items.serializeNBT(registries).let { tag.put("items", it) }
		this.energy.serializeNBT(registries).let { tag.put("energy", it) }
		this.tank.writeToNBT(registries, tag)
	}

	override fun loadAdditional(tag : CompoundTag, registries : Provider) {
		super.loadAdditional(tag, registries)
		this.items.deserializeNBT(registries, tag.getCompound("items"))
		this.energy.deserializeNBT(registries, tag.get("energy") as IntTag)
		this.tank.readFromNBT(registries, tag)
	}

	override fun getUpdateTag(registries : Provider) : CompoundTag {
		val tag = CompoundTag()
		this.saveAdditional(tag, registries)
		return tag
	}

	override fun getUpdatePacket() : Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
}