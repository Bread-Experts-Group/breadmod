package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs

import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen

abstract class AbstractToolGunScreenTab(
	id: String,
	val tabBarColor: Int,
	screen: ToolGunScreen,
	val stack: ItemStack
) : ContainerWidget<ToolGunScreen>(0, 0, 243, 185, id, screen) {
	abstract fun getTabButton(): TabButton
}