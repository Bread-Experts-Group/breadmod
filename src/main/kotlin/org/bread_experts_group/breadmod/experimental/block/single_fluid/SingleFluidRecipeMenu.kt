package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class SingleFluidRecipeMenu(
    id: Int,
    inventory: Inventory,
    val parent: SingleFluidRecipeBlockEntity
) : AbstractContainerMenu(ModMenuTypes.SINGLE_FLUID.get(), id) {
    constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
        id, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SINGLE_FLUID_TEST.get())
            .get()
    )

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player): Boolean = true
}