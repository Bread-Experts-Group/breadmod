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
    pos: BlockPos, state: BlockState
) : BlockEntity(
    ModBlockEntityTypes.DOUGH_MACHINE.get(),
    pos,
    state
), MenuProvider, CraftingContainer, WorldlyContainer {
    var progress: Int = 0
    var maxProgress: Int = 0

    val energyHandler: EnergyStorage by lazy {
        object : EnergyStorage(100000) {
            override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
                syncToClients()
                return super.receiveEnergy(toReceive, simulate)
            }
        }
    }

    val fluidHandler: FluidTank by lazy {
        object : FluidTank(10000) {
            override fun onContentsChanged() {
                syncToClients()
            }

            override fun getTanks(): Int = 2
        }
    }

    val horizontal: Direction = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
    val sidedInvWrapper: SidedInvWrapper = SidedInvWrapper(this, horizontal)

    private var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(3, ItemStack.EMPTY)

    private fun syncToClients() = level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)

    override fun clearContent(): Unit = itemSlots.forEach { it.count = 0 }

    override fun getContainerSize(): Int = itemSlots.size
    override fun isEmpty(): Boolean = itemSlots.any { !it.isEmpty }
    override fun getItem(slot: Int): ItemStack = itemSlots[slot]
    override fun removeItem(slot: Int, pAmount: Int): ItemStack = itemSlots[slot].split(pAmount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = itemSlots[slot].copyAndClear()
    override fun setItem(slot: Int, stack: ItemStack) {
        itemSlots[slot] = stack
    }

    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

    override fun fillStackedContents(contents: StackedContents) {
        for (stack: ItemStack in itemSlots) {
            contents.accountSimpleStack(stack)
        }
    }

    // allow every face of the block to receive and extract items
    override fun getSlotsForFace(side: Direction): IntArray = intArrayOf(0, 1, 2)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        if (direction != null) getSlotsForFace(direction).contains(index) && index == 0 else true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        getSlotsForFace(direction).contains(index) && ((index == 2 && stack.`is`(Items.BUCKET)) || index != 0)

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = itemSlots

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("energy", energyHandler.serializeNBT(registries))
        tag.put("fluid", CompoundTag().also { fluidHandler.writeToNBT(registries, it) })
        tag.putInt("progress", progress)
        tag.putInt("maxProgress", maxProgress)

        ContainerHelper.saveAllItems(tag, itemSlots, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        energyHandler.deserializeNBT(registries, tag.get("energy") ?: return)
        fluidHandler.readFromNBT(registries, tag.getCompound("fluid"))
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("maxProgress")

        itemSlots = NonNullList.withSize(3, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, itemSlots, registries)
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        DoughMachineMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = modTranslatable("block", "dough_machine")
}