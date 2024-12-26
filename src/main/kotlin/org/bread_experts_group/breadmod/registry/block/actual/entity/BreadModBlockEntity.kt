package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
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
import net.neoforged.neoforge.items.ItemStackHandler
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTank
import org.bread_experts_group.breadmod.registry.Registry.logger

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

	val items : ItemStackHandler? = if (this.itemSlots == 0) null else ItemStackHandler(this.itemSlots)
	val energy : SyncedEnergy? = if (this.energyCapacity == 0) null else this.SyncedEnergy(this.energyCapacity)
	val tank : SidedFluidTank? = if (this.fluidTanks.isEmpty()) null else SidedFluidTank(
		this.fluidTanks.map { this.SyncedFluidHandler(it.first, it.second, it.third) }
	)
	var energyDivision : Int? = null
	fun dropContents() {
		this.items?.let { item ->
			val list = NonNullList.createWithCapacity<ItemStack>(item.slots)
			repeat(item.slots) {
				list.add(item.getStackInSlot(it))
				this.items.setStackInSlot(it, ItemStack.EMPTY)
			}
			this.level?.let { Containers.dropContents(it, this.blockPos, list) }
		}
	}

	fun getFluid(tank : Int) : FluidStack =
		if (this.tank != null) this.tank.getFluidInTank(tank) else FluidStack.EMPTY

	fun setFluid(tank : Int, stack : FluidStack) : Unit =
		if (this.tank != null) this.tank.tanks[tank].fluid = stack else logger.error("Fluid handler is null!")

	fun growFluid(tank : Int, amount : Int) : Unit = this.getFluid(tank).grow(amount)
	fun getItem(slot : Int) : ItemStack =
		if (this.items != null) this.items.getStackInSlot(slot) else ItemStack.EMPTY

	fun setItem(slot : Int, stack : ItemStack) : Unit =
		if (this.items != null) this.items.setStackInSlot(slot, stack) else logger.error("Item handler is null!")

	fun growItem(slot : Int, count : Int) : Unit = this.getItem(slot).grow(count)
	//	fun shrinkItem(slot : Int, count : Int) : Unit = this.getItem(slot).shrink(count)
	private fun updateClients() =
		this@BreadModBlockEntity.level?.sendBlockUpdated(
			this@BreadModBlockEntity.blockPos,
			this@BreadModBlockEntity.blockState,
			this@BreadModBlockEntity.blockState,
			Block.UPDATE_CLIENTS
		)

	override fun saveAdditional(tag : CompoundTag, registries : Provider) {
		super.saveAdditional(tag, registries)
		this.items?.serializeNBT(registries)?.let { tag.put("items", it) }
		this.energy?.serializeNBT(registries)?.let { tag.put("energy", it) }
		this.tank?.writeToNBT(registries, tag)
	}

	override fun loadAdditional(tag : CompoundTag, registries : Provider) {
		super.loadAdditional(tag, registries)
		this.items?.deserializeNBT(registries, tag.getCompound("items"))
		this.energy?.deserializeNBT(registries, tag.get("energy") ?: return)
		this.tank?.readFromNBT(registries, tag)
	}

	override fun getUpdateTag(registries : Provider) : CompoundTag {
		val tag = CompoundTag()
		this.saveAdditional(tag, registries)
		return tag
	}

	override fun getUpdatePacket() : Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
}