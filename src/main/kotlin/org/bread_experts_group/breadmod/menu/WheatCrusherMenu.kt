package org.bread_experts_group.breadmod.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.block.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class WheatCrusherMenu(
    id: Int,
    inventory: Inventory,
    parent: WheatCrusherBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.WHEAT_CRUSHER.get(), id) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.WHEAT_CRUSHER.get()).get()
    )

    override val containerSlotCount: Int = 2

    init {
        addInventorySlots(inventory, 8, 174, 116)
        addSlot(Slot(parent, 0, 80, 15))
        addSlot(ResultSlot(1, 80, 87, parent))
    }

    override fun stillValid(player: Player): Boolean = player.containerMenu == this
}