package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.network.serverbound.ComputerKeystrokePacket
import java.awt.Color

class KeyboardScreen(private val monitorPos: BlockPos) : Screen(Component.empty()) {
	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (super.keyPressed(keyCode, scanCode, modifiers)) return true
		PacketDistributor.sendToServer(ComputerKeystrokePacket(this.monitorPos, keyCode, modifiers))
		return true
	}

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.drawString(
			localClient.font,
			Component.literal("Keyboard capture active.").withStyle(ChatFormatting.GOLD),
			10, 10, Color.WHITE.rgb
		)
	}

	override fun isPauseScreen(): Boolean = false
}