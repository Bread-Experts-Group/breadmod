package org.bread_experts_group.breadmod.experimental.block.multi_item

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.experimental.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlot

class MultiItemRecipeMenu(
    id: Int,
    inventory: Inventory,
    parent: MultiItemRecipeBlockEntity
) : AbstractTestRecipeMenu(ModMenuTypes.MULTI_ITEM.get(), id, inventory, parent) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.MULTI_ITEM_TEST.get()).get()
    )

    init {
        addSlot(Slot(parent, 0, 15, 30))
        addSlot(Slot(parent, 1, 30, 30))
        addSlot(Slot(parent, 2, 45, 30))
        addSlot(ResultSlot(3, 80, 30, parent))
    }

    override val containerSlotCount: Int = 4
}