package org.bread_experts_group.breadmod.client

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper

// todo GuiBuilder will be a reality soon...
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
		BreadModTextureHelper(this.containerElementLocation("energy_meter_16x47"), textureHeight = 47)
	val VERTICAL_ARROW_9X48: BreadModTextureHelper = BreadModTextureHelper(
		this.containerElementLocation("vertical_arrow_9x48"),
		9, 48
	)
	val FILLED_VERTICAL_ARROW_9X48: BreadModTextureHelper = BreadModTextureHelper(
		this.containerElementLocation("filled_vertical_arrow_9x48"),
		9, 48
	)
	val DOUGH_MACHINE_ARROW: BreadModTextureHelper =
		BreadModTextureHelper(this.containerElementLocation("dough_machine_arrow"), 24, 17)
	val DOUGH_MACHINE_ARROW_FILLED: BreadModTextureHelper =
		BreadModTextureHelper(this.containerElementLocation("dough_machine_arrow_filled"), 24, 17)
	val CUBE_BI_DIRECTIONAL: BreadModTextureHelper =
		BreadModTextureHelper(this.guiElementLocation("cube_sprites"), 256, 256)
	val CUBE_IN_ONLY: BreadModTextureHelper =
		BreadModTextureHelper(this.guiElementLocation("cube_sprites_in"), 256, 256)
	val CUBE_OUT_ONLY: BreadModTextureHelper =
		BreadModTextureHelper(this.guiElementLocation("cube_sprites_out"), 256, 256)

	// todo figure out nine sliced from vanilla
	// tab.png.mcmeta / tab.png
	val NINE_SLICED_BG: BreadModTextureHelper =
		BreadModTextureHelper(modLocation("textures", "gui", "test.png"), 32, 32)

	private fun toolGunHudLocation(name: String): ResourceLocation =
		modLocation("textures", "tool_gun", "hud", "$name.png")

	private fun toolGunGuiLocation(name: String): ResourceLocation =
		modLocation("textures", "tool_gun", "gui", "$name.png")

	private fun containerElementLocation(name: String): ResourceLocation =
		modLocation("textures", "gui", "container", "elements", "$name.png")

	private fun guiElementLocation(name: String): ResourceLocation =
		modLocation("textures", "gui", "$name.png")
}