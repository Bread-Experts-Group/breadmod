package org.bread_experts_group.breadmod.registry

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

object KeyMappings {
	@DataGenerateLanguage("en_us")
	val categoryLangKey: String = "controls.${BreadMod.ID}"

	/**
	 * Key for placing an item in world.
	 */
	@DataGenerateLanguage("en_us")
	val placeItemKey: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.place_item_in_world",
		KeyConflictContext.IN_GAME,
		KeyModifier.NONE,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_COMMA),
		this.categoryLangKey
	)
	// Tool Gun specific keys
	/**
	 * Key for opening the mode change gui in the tool gun.
	 */
	@DataGenerateLanguage("en_us", "Tool Gun: Open mode screen")
	val openModeGui: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.tool_gun_mode_screen",
		KeyConflictContext.IN_GAME,
		KeyModifier.SHIFT,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_R),
		this.categoryLangKey
	)

	@DataGenerateLanguage("en_us", "Tool Gun: Alt control 1")
	val toolGunAltOne: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.tool_gun_alt_one",
		KeyConflictContext.IN_GAME,
		KeyModifier.SHIFT,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_Y),
		this.categoryLangKey
	)

	@DataGenerateLanguage("en_us", "Tool Gun: Alt control 2")
	val toolGunAltTwo: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.tool_gun_alt_two",
		KeyConflictContext.IN_GAME,
		KeyModifier.SHIFT,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_U),
		this.categoryLangKey
	)

	@DataGenerateLanguage("en_us", "Tool Gun: Alt control 3")
	val toolGunAltThree: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.tool_gun_alt_three",
		KeyConflictContext.IN_GAME,
		KeyModifier.ALT,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_I),
		this.categoryLangKey
	)

	@DataGenerateLanguage("en_us", "Tool Gun: Alt control 4")
	val toolGunAltFour: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.tool_gun_alt_four",
		KeyConflictContext.IN_GAME,
		KeyModifier.ALT,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_O),
		this.categoryLangKey
	)
}