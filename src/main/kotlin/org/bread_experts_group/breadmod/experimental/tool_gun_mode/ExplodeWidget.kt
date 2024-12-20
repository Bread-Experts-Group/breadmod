package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class ExplodeWidget : ModeWidget(Component.literal("Explode"), Blocks.TNT.asItem().defaultInstance) {
	override val previewImage : ResourceLocation = modLocation("textures", "gui", "tool_gun", "exploder.png")
	override val modeName : Component = Component.literal("Explode Mode")
	override val modeDescription : Component = Component.literal(
		"BOOM BOOM BOOM, I CAN'T SINGING THIS BLOODY TUNE TUNE TUNE IT'S GONNA MAKE MY BRAIN GO BOOM BOOM BOOM-"
	)
}