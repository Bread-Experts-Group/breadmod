package org.bread_experts_group.breadmod.client.gui

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.registry.ModFonts
import java.awt.Color

class IA32ComputerOutputOverlay : LayeredDraw.Layer {
	companion object {
		var output: String = "Bread BIOS v1.0\nStarting up... (DL = 0xE0, CD)\n\n"
	}

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		if (BreadMod.computerExp.state == Thread.State.NEW) BreadMod.computerExp.start()
		guiGraphics.fill(0, 0, 1000, 1000, Color.BLACK.rgb)
		Companion.output.split('\n').forEachIndexed { i, s ->
			guiGraphics.drawString(
				localClient.font,
				Component.literal(s).withStyle(ModFonts.IBM_EGA_9_14),
				0, i * 9, Color.LIGHT_GRAY.rgb
			)
		}
		listOf(
			BreadMod.processor.a,
			BreadMod.processor.c,
			BreadMod.processor.d,
			BreadMod.processor.b,
			BreadMod.processor.sp,
			BreadMod.processor.bp,
			BreadMod.processor.si,
			BreadMod.processor.di,
			BreadMod.processor.ip,
			BreadMod.processor.flags,
			BreadMod.processor.cs,
			BreadMod.processor.ss,
			BreadMod.processor.ds,
			BreadMod.processor.es,
			BreadMod.processor.fs,
			BreadMod.processor.gs,
			BreadMod.processor.cr0
		).forEachIndexed { i, r ->
			val component = Component.literal("${r.name}: ${hex(r.rx)}").withStyle(ModFonts.IBM_EGA_9_14)
			guiGraphics.drawString(
				localClient.font, component,
				localClient.window.guiScaledWidth - localClient.font.width(component), i * 9,
				Color.ORANGE.rgb
			)
		}
	}
}