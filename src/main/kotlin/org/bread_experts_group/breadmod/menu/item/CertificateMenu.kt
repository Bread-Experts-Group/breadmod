package org.bread_experts_group.breadmod.menu.item

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.menu.AbstractModContainerMenu
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class CertificateMenu(
    id: Int,
    val inventory: Inventory
) : AbstractModContainerMenu(ModMenuTypes.CERTIFICATE.get(), id) {
    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = player.inventory.contains(ModItems.CERTIFICATE.toStack())
}