package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class SingleFluidRecipeMenu(
    id: Int,
    inventory: Inventory,
    parent: SingleFluidRecipeBlockEntity
) : AbstractTestRecipeMenu(ModMenuTypes.SINGLE_FLUID.get(), id, inventory, parent) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SINGLE_FLUID_TEST.get())
            .get()
    )
}