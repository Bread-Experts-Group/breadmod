package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class ExplodeWidget : ModeWidget(
	Component.literal("Explode"),
	Blocks.TNT.asItem().defaultInstance,
	modLocation("textures", "gui", "tool_gun", "exploder.png"),
	Component.literal("Explode Mode"),
	Component.literal(
		"BOOM BOOM BOOM, I CAN'T SINGING THIS BLOODY TUNE TUNE TUNE IT'S GONNA MAKE MY BRAIN GO BOOM BOOM BOOM-"
	)
)