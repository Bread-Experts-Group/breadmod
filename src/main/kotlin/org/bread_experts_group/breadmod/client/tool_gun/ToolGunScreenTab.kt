package org.bread_experts_group.breadmod.client.tool_gun

import org.bread_experts_group.breadmod.client.screen.ContainerWidget
import java.awt.Color

abstract class ToolGunScreenTab(
	x: Int,
	y: Int,
	id: String,
	val tabBarColor: Color
) : ContainerWidget(x, y, 243, 185, id) {
	abstract fun getTabButton(): TabButton
}