package org.bread_experts_group.breadmod.client.tool_gun

import org.bread_experts_group.breadmod.client.screen.ContainerWidget
import java.awt.Color

abstract class ToolGunScreenTab(
	val id: String,
	val tabBarColor: Color
) : ContainerWidget(0, 0, 243, 185, id) {
	abstract fun getTabButton(): TabButton
}