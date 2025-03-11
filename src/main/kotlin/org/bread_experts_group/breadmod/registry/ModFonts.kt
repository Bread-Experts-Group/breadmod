package org.bread_experts_group.breadmod.registry

import net.minecraft.network.chat.Style
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

object ModFonts {
	/**
	 * Filled-in variant of the war timer font.
	 * @author Logan McLean (this property), Miko Elbrecht (translation of sprites into font), Tour De Pizza (sprites)
	 * @since 1.0.0
	 */
	val WARTIMER_INFILL: Style = Style.EMPTY.withFont(modLocation("wartimer_infill"))

	/**
	 * IBM VGA 9x14 BIOS font
	 * @author Miko Elbrecht (this property), IBM (sprites)
	 * @since 1.0.0
	 */
	val IBM_VGA_9_14: Style = Style.EMPTY.withFont(modLocation("px437_ibm_vga_9_14"))
}