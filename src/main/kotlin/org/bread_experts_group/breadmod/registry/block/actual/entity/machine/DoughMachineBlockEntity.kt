package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu

class DoughMachineBlockEntity(
	pos : BlockPos, state : BlockState
) : BlockEntity(
	ModBlockEntityTypes.DOUGH_MACHINE.get(),
	pos,
	state
), MenuProvider, CraftingContainer, WorldlyContainer {
	var progress : Int = 0
	var maxProgress : Int = 0
	val energyHandler : EnergyStorage by lazy {
		object : EnergyStorage(100000) {
			override fun receiveEnergy(toReceive : Int, simulate : Boolean) : Int {
				this@DoughMachineBlockEntity.syncToClients()
				return super.receiveEnergy(toReceive, simulate)
			}
		}
	}
	val fluidHandler : FluidTank by lazy {
		object : FluidTank(10000) {
			override fun onContentsChanged() {
				this@DoughMachineBlockEntity.syncToClients()
			}

			override fun getTanks() : Int = 2
		}
	}
	val horizontal : Direction = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
	val sidedInvWrapper : SidedInvWrapper = SidedInvWrapper(this, this.horizontal)
	private var itemSlots : NonNullList<ItemStack> = NonNullList.withSize(3, ItemStack.EMPTY)
	private fun syncToClients() =
		this.level?.sendBlockUpdated(this.blockPos, this.blockState, this.blockState, Block.UPDATE_CLIENTS)

	override fun clearContent() : Unit = this.itemSlots.forEach { it.count = 0 }
	override fun getContainerSize() : Int = this.itemSlots.size
	override fun isEmpty() : Boolean = this.itemSlots.any { !it.isEmpty }
	override fun getItem(slot : Int) : ItemStack = this.itemSlots[slot]
	override fun removeItem(slot : Int, pAmount : Int) : ItemStack = this.itemSlots[slot].split(pAmount)
	override fun removeItemNoUpdate(slot : Int) : ItemStack = this.itemSlots[slot].copyAndClear()
	override fun setItem(slot : Int, stack : ItemStack) {
		this.itemSlots[slot] = stack
	}

	override fun stillValid(player : Player) : Boolean = Container.stillValidBlockEntity(this, player)
	override fun fillStackedContents(contents : StackedContents) {
		for (stack : ItemStack in this.itemSlots) {
			contents.accountSimpleStack(stack)
		}
	}
	// allow every face of the block to receive and extract items
	override fun getSlotsForFace(side : Direction) : IntArray = intArrayOf(0, 1, 2)
	override fun canPlaceItemThroughFace(index : Int, itemStack : ItemStack, direction : Direction?) : Boolean =
		if (direction != null) this.getSlotsForFace(direction).contains(index) && index == 0 else true

	override fun canTakeItemThroughFace(index : Int, stack : ItemStack, direction : Direction) : Boolean =
		this.getSlotsForFace(direction).contains(index) && ((index == 2 && stack.`is`(Items.BUCKET)) || index != 0)

	override fun getWidth() : Int = 1
	override fun getHeight() : Int = 1
	override fun getItems() : MutableList<ItemStack> = this.itemSlots
	override fun getUpdateTag(registries : HolderLookup.Provider) : CompoundTag =
		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }

	override fun getUpdatePacket() : Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)

	override fun saveAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.saveAdditional(tag, registries)
		tag.put("energy", this.energyHandler.serializeNBT(registries))
		tag.put("fluid", CompoundTag().also { this.fluidHandler.writeToNBT(registries, it) })
		tag.putInt("progress", this.progress)
		tag.putInt("maxProgress", this.maxProgress)

		ContainerHelper.saveAllItems(tag, this.itemSlots, registries)
	}

	override fun loadAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.loadAdditional(tag, registries)
		this.energyHandler.deserializeNBT(registries, tag.get("energy") ?: return)
		this.fluidHandler.readFromNBT(registries, tag.getCompound("fluid"))
		this.progress = tag.getInt("progress")
		this.maxProgress = tag.getInt("maxProgress")

		this.itemSlots = NonNullList.withSize(3, ItemStack.EMPTY)
		ContainerHelper.loadAllItems(tag, this.itemSlots, registries)
	}

	override fun createMenu(containerId : Int, playerInventory : Inventory, player : Player) : AbstractContainerMenu =
		DoughMachineMenu(containerId, playerInventory, this)

	override fun getDisplayName() : Component = modTranslatable("block", "dough_machine")
}