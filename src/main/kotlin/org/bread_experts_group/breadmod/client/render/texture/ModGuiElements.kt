package org.bread_experts_group.breadmod.client.render.texture

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

/**
 * Holds [ResourceLocation]s for Breadmod's gui elements.
 */
object ModGuiElements {
	val WAR_TIMER: GuiElement = GuiElement(
		modLocation("textures", "gui", "hud", "war_overlay_timer.png"),
		329, 111
	)

	// Tool Gun Specific //
	val MODE_OVERLAY_BG: GuiElement = GuiElement(
		this.toolGunHudLocation("mode_overlay_bg"),
		166, 41
	)
	val MOUSE: GuiElement = GuiElement(this.toolGunHudLocation("mouse"))
	val MOUSE_LEFT: GuiElement = GuiElement(this.toolGunHudLocation("mouse_left"))
	val MOUSE_RIGHT: GuiElement = GuiElement(this.toolGunHudLocation("mouse_right"))
	val MOUSE_MIDDLE: GuiElement = GuiElement(this.toolGunHudLocation("mouse_middle"))
	val INFO: GuiElement = GuiElement(
		this.toolGunHudLocation("info_icon"),
		8, 8
	)
	val FRAME: GuiElement = GuiElement(
		this.toolGunGuiLocation("frame"),
		256, 256
	)
	val SCREEN: GuiElement = GuiElement(
		modLocation("textures", "tool_gun", "render", "screen.png"),
		9, 8
	)

	// Mode Preview Images
	val EXPLODE_PREVIEW: GuiElement = GuiElement(this.toolGunGuiLocation("exploder"), 854, 480)
	val POWER_PREVIEW: GuiElement = GuiElement(modLocation("powermode"), 854, 480)

	// Gui Elements //
	val CUBE_BI_DIRECTIONAL: GuiElement =
		GuiElement(this.guiElementLocation("cube_sprites"), 256, 256)
	val CUBE_IN_ONLY: GuiElement =
		GuiElement(this.guiElementLocation("cube_sprites_in"), 256, 256)
	val CUBE_OUT_ONLY: GuiElement =
		GuiElement(this.guiElementLocation("cube_sprites_out"), 256, 256)

	// Sprites //
	val ENERGY_METER: GuiElement = GuiElement(modLocation("container", "elements", "energy_meter"), textureHeight = 47)
	val SLOT: GuiElement = GuiElement(modLocation("container", "slot"), 18, 18)
	val RESULT_SLOT: GuiElement = GuiElement.ofScaledCopy(this.SLOT, 26, 26)
	val BUCKET_SLOT: GuiElement = GuiElement(modLocation("container", "elements", "bucket_slot"), 18, 18)
	val PLUS: GuiElement = GuiElement(modLocation("container", "elements", "plus"), 13, 13)
	val INVENTORY_SLOTS: GuiElement = GuiElement(modLocation("container", "inventory_slots"), 162, 54)
	val HOTBAR_SLOTS: GuiElement = GuiElement(modLocation("container", "hotbar_slots"), 162, 18)
	val BACKGROUND: GuiElement = GuiElement(modLocation("background"), 32, 32)
	val BACKGROUND_ALT: GuiElement = GuiElement(modLocation("background_alt"), 20, 20)
	val FLAME: GuiElement = GuiElement(modLocation("flame"), 14, 14)
	val FLAT_BACKGROUND: GuiElement = GuiElement.ofSolidColor(198, 198, 198)
	val FLOPPY_DISK: GuiElement = GuiElement(modLocation("computer", "floppy_disk"))

	// Wheat Crusher
	val WHEAT_CRUSHER_LEFT_WHEEL: GuiElement =
		GuiElement(modLocation("container", "wheat_crusher", "wheel_left"), 32, 32)
	val WHEAT_CRUSHER_RIGHT_WHEEL: GuiElement =
		GuiElement(modLocation("container", "wheat_crusher", "wheel_right"), 32, 32)
	val WHEAT_CRUSHER_ARROW: GuiElement =
		GuiElement(modLocation("container", "wheat_crusher", "vertical_arrow"), 9, 48)
	val WHEAT_CRUSHER_ARROW_FILLED: GuiElement =
		GuiElement(modLocation("container", "wheat_crusher", "vertical_arrow_filled"), 9, 48)

	// Dough Machine
	val DOUGH_MACHINE_ARROW: GuiElement =
		GuiElement(modLocation("container", "dough_machine", "dough_machine_arrow"), 24, 17)
	val DOUGH_MACHINE_ARROW_FILLED: GuiElement =
		GuiElement(modLocation("container", "dough_machine", "dough_machine_arrow_filled"), 24, 17)
	val DOUGH_MACHINE_ARROW_JEI: GuiElement =
		GuiElement(modLocation("container", "dough_machine", "jei", "arrow"), 76, 21)
	val DOUGH_MACHINE_ARROW_FILLED_JEI: GuiElement =
		GuiElement(modLocation("container", "dough_machine", "jei", "arrow_filled"), 76, 21)

	// Toaster
	val TOASTER_ARROW_JEI: GuiElement =
		GuiElement(modLocation("container", "toaster", "jei", "arrow"), 29, 22)
	val TOASTER_ARROW_FILLED_JEI: GuiElement =
		GuiElement(modLocation("container", "toaster", "jei", "arrow_filled"), 29, 22)

	private fun toolGunHudLocation(name: String): ResourceLocation =
		modLocation("textures", "tool_gun", "hud", "$name.png")

	private fun toolGunGuiLocation(name: String, extension: String = "png"): ResourceLocation =
		modLocation("textures", "tool_gun", "gui", "$name.$extension")

	private fun guiElementLocation(name: String): ResourceLocation =
		modLocation("textures", "gui", "$name.png")
}