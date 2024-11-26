package org.bread_experts_group.breadmod.registry.block.actual.experimental

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.AbstractModContainerMenu
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlot

class MultiItemRecipeMenu(
    id: Int,
    inventory: Inventory,
    val parent: MultiItemRecipeBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.MULTI_ITEM.get(), id) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.MULTI_ITEM_TEST.get()).get()
    )

    init {
        addInventorySlots(inventory, 8, 174, 116)
        addSlot(Slot(parent, 0, 15, 30))
        addSlot(Slot(parent, 1, 30, 30))
        addSlot(Slot(parent, 2, 45, 30))
        addSlot(ResultSlot(3, 80, 30, parent))
    }
}