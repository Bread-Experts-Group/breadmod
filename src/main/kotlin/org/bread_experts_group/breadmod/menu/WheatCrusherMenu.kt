package org.bread_experts_group.breadmod.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.block.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class WheatCrusherMenu(
    id: Int,
    inventory: Inventory,
    val parent: WheatCrusherBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.WHEAT_CRUSHER.get(), id) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.WHEAT_CRUSHER.get()).get()
    )

    fun getScaledProgress(): Int = ((parent.progress.toFloat() / parent.maxProgress.toFloat()) * 48).toInt()

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

    override val containerSlotCount: Int = 2

    init {
        addInventorySlots(inventory, 8, 174, 116)
        addSlot(Slot(parent, 0, 80, 15))
        addSlot(ResultSlot(1, 80, 87, parent))
    }
}