package org.bread_experts_group.breadmod.client.gui

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper

/**
 * Holds [ResourceLocation]s for Breadmod's textures
 */
object ModTextureLocations {
	val WAR_TIMER : BreadModTextureHelper = BreadModTextureHelper(
		modLocation("textures", "gui", "hud", "war_overlay_timer.png"),
		329, 111
	)

	// Tool Gun Specific
	val MODE_OVERLAY_BG : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mode_overlay_bg"),
		166, 41
	)
	val MOUSE : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mouse"),
		16, 16
	)
	val MOUSE_LEFT : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mouse_left"),
		16, 16
	)
	val MOUSE_RIGHT : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mouse_right"),
		16, 16
	)
	val MOUSE_MIDDLE : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mouse_middle"),
		16, 16
	)
	val INFO : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("info_icon"),
		8, 8
	)

	// Mode Preview Images
	val EXPLODE_PREVIEW : BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunGuiLocation( "exploder"),
		854, 480
	)

	private fun toolGunHudLocation(name : String) : ResourceLocation =
		modLocation("textures", "tool_gun", "hud", "$name.png")
	private fun toolGunGuiLocation(name : String) : ResourceLocation =
		modLocation("textures", "tool_gun", "gui", "$name.png")
}