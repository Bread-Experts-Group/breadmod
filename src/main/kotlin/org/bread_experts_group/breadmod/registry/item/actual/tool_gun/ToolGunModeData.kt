package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import org.bread_experts_group.breadmod.experimental.tool_gun_mode.ExplodeWidget
import org.bread_experts_group.breadmod.experimental.tool_gun_mode.ModeWidget

object ToolGunModeData {
	var name : String = ""
	val modeWidgets : MutableList<ModeWidget> = mutableListOf(
		ExplodeWidget()
	)
}