package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.tool_gun_mode.ExplodeWidget
import org.bread_experts_group.breadmod.experimental.tool_gun_mode.ModeWidget
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunMode

object ToolGunModeData {
    var name: String = ""
    var currentMode: ResourceLocation = modLocation()
    val modes: MutableList<ToolGunMode> = mutableListOf()

    val modeWidgets: MutableList<ModeWidget> = mutableListOf(
        ExplodeWidget()
    )
}