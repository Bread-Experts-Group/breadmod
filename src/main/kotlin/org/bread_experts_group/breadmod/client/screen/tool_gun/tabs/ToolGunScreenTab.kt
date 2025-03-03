package org.bread_experts_group.breadmod.client.screen.tool_gun.tabs

import org.bread_experts_group.breadmod.client.screen.ContainerWidget
import org.bread_experts_group.breadmod.client.screen.tool_gun.ToolGunScreen
import org.bread_experts_group.breadmod.client.screen.tool_gun.widgets.TabButton
import java.awt.Color

abstract class ToolGunScreenTab(
	val id: String,
	val tabBarColor: Color,
	screen: ToolGunScreen
) : ContainerWidget<ToolGunScreen>(0, 0, 243, 185, id, screen) {
	abstract fun getTabButton(): TabButton
}