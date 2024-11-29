package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.util.isTag
import kotlin.jvm.optionals.getOrNull

class DoughMachineMenu(
    id: Int,
    inventory: Inventory,
    val parent: DoughMachineBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.DOUGH_MACHINE.get(), id) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.DOUGH_MACHINE.get()).get()
    )

    fun getScaledProgress(): Int = ((parent.progress.toFloat() / parent.maxProgress.toFloat()) * 24).toInt()

    fun getEnergyStoredScaled(): Int {
        return (parent.level ?: return 0).getCapability(
            Capabilities.EnergyStorage.BLOCK,
            parent.blockPos,
            parent.horizontal
        )?.let { ((it.energyStored.toFloat() / it.maxEnergyStored) * 47).toInt() } ?: 0
    }

    fun getEnergyHandler(): IEnergyStorage? =
        parent.level?.getCapability(Capabilities.EnergyStorage.BLOCK, parent.blockPos, parent.horizontal)

    fun isCrafting(): Boolean = parent.progress > 0

    override val containerSlotCount: Int = 3

    class DoughMachineBucketSlot(parent: DoughMachineBlockEntity) : Slot(parent, 2, 153, 7) {
        override fun mayPlace(stack: ItemStack): Boolean =
            stack.item.let { it is BucketItem && isTag(FluidTags.WATER) } ||
                    FluidUtil.getFluidHandler(stack).getOrNull().let {
                        it?.drain(1, IFluidHandler.FluidAction.SIMULATE)
                            ?.let { drained -> drained.amount == 1 && isTag(FluidTags.WATER) } == true
                    }
    }

    init {
        addInventorySlots(inventory, 8, 142, 84)
        addSlot(Slot(parent, 0, 26, 34))
        addSlot(ResultSlot(1, 78, 35, parent))
        addSlot(DoughMachineBucketSlot(parent))
    }
}