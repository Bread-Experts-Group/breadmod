package org.bread_experts_group.breadmod.client.gui

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper

/**
 * Holds [ResourceLocation]s for Breadmod's textures
 */
object ModTextureLocations {
	val WAR_TIMER: BreadModTextureHelper = BreadModTextureHelper(
		modLocation("textures", "gui", "hud", "war_overlay_timer.png"),
		329, 111
	)

	// Tool Gun Specific
	val MODE_OVERLAY_BG: BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("mode_overlay_bg"),
		166, 41
	)
	val MOUSE: BreadModTextureHelper = BreadModTextureHelper(this.toolGunHudLocation("mouse"))
	val MOUSE_LEFT: BreadModTextureHelper = BreadModTextureHelper(this.toolGunHudLocation("mouse_left"))
	val MOUSE_RIGHT: BreadModTextureHelper = BreadModTextureHelper(this.toolGunHudLocation("mouse_right"))
	val MOUSE_MIDDLE: BreadModTextureHelper = BreadModTextureHelper(this.toolGunHudLocation("mouse_middle"))
	val INFO: BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunHudLocation("info_icon"),
		8, 8
	)
	val FRAME: BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunGuiLocation("frame"),
		256, 256
	)
	val SCREEN: BreadModTextureHelper = BreadModTextureHelper(
		modLocation("textures", "tool_gun", "render", "screen.png"),
		9, 8
	)

	// Mode Preview Images
	val EXPLODE_PREVIEW: BreadModTextureHelper = BreadModTextureHelper(
		this.toolGunGuiLocation("exploder"),
		854, 480
	)

	// todo convert the existing mod guis to this system
	// Gui Elements
	val ENERGY_METER_16X47: BreadModTextureHelper =
		BreadModTextureHelper(this.guiElementLocation("energy_meter_16x47"), textureHeight = 47)
	val VERTICAL_ARROW_9X48: BreadModTextureHelper = BreadModTextureHelper(
		this.guiElementLocation("vertical_arrow_9x48"),
		9, 48
	)
	val FILLED_VERTICAL_ARROW_9X48: BreadModTextureHelper = BreadModTextureHelper(
		this.guiElementLocation("filled_vertical_arrow_9x48"),
		9, 48
	)
	val CUBE_BI_DIRECTIONAL: BreadModTextureHelper =
		BreadModTextureHelper(modLocation("textures", "gui", "cube_sprites.png"), 256, 256)
	val CUBE_IN_ONLY: BreadModTextureHelper =
		BreadModTextureHelper(modLocation("textures", "gui", "cube_sprites_in.png"), 256, 256)
	val CUBE_OUT_ONLY: BreadModTextureHelper =
		BreadModTextureHelper(modLocation("textures", "gui", "cube_sprites_out.png"), 256, 256)

	// todo figure out nine sliced from vanilla
	// tab.png.mcmeta / tab.png
	val NINE_SLICED_BG: BreadModTextureHelper =
		BreadModTextureHelper(modLocation("textures", "gui", "test.png"), 32, 32)

	private fun toolGunHudLocation(name: String): ResourceLocation =
		modLocation("textures", "tool_gun", "hud", "$name.png")

	private fun toolGunGuiLocation(name: String): ResourceLocation =
		modLocation("textures", "tool_gun", "gui", "$name.png")

	private fun guiElementLocation(name: String): ResourceLocation =
		modLocation("textures", "gui", "container", "elements", "$name.png")
}