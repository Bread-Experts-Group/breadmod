package org.bread_experts_group.breadmod.registry

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import org.bread_experts_group.breadmod.BreadMod

object KeyMappings {
	/**
	 * Key for opening the mode change gui in the tool gun.
	 */
	val openModeGui: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.mode_screen",
		KeyConflictContext.UNIVERSAL,
		KeyModifier.NONE,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_R),
		"controls.${BreadMod.ID}"
	)

	/**
	 * Key for placing an item in world.
	 */
	val placeItemKey: KeyMapping = KeyMapping(
		"controls.${BreadMod.ID}.place_item",
		KeyConflictContext.IN_GAME,
		KeyModifier.NONE,
		InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_COMMA),
		"controls.${BreadMod.ID}"
	)
}