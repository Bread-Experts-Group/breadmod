package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs

import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import java.awt.Color

abstract class ToolGunScreenTab(
	val id: String,
	val tabBarColor: Color,
	screen: ToolGunScreen
) : ContainerWidget<ToolGunScreen>(0, 0, 243, 185, id, screen) {
	abstract fun getTabButton(): TabButton
}