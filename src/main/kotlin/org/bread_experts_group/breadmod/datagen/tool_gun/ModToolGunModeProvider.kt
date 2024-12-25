package org.bread_experts_group.breadmod.datagen.tool_gun

import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ExplodeMode

internal class ModToolGunModeProvider(
	output : PackOutput
) : ToolGunModeProvider(output, BreadMod.ID) {
	override fun addModes() {
		this.addMode(
			"explode",
			Component.literal("explode"),
			Component.literal("tooltip"),
			ExplodeMode::class.java
		)
	}
}