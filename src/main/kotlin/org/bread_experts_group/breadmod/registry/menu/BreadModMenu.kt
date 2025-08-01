package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

abstract class BreadModMenu(
	menuType: MenuType<*>,
	id: Int,
	val inventory: Inventory,
	val entity: BreadModBlockEntity
) : AbstractContainerMenu(menuType, id) {
	abstract fun ofScreen(title: Component): BreadModScreen
}