package org.bread_experts_group.breadmod.client.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.MonitorBlockEntity
import java.awt.Color

class KeyboardScreen(private val monitorPos: BlockPos) : Screen(Component.empty()) {
	// todo remove after fixing packet not notifying the BER
	private val monitorEntity = localClient.level?.getBlockEntity(this.monitorPos) as MonitorBlockEntity
	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		this.monitorEntity.computer.keyboard.write(keyCode.toUByte())
		// todo figure out why this isn't sending the change to clients later
//		PacketDistributor.sendToServer(ComputerKeystrokePacket(this.monitorPos, keyCode))
		return super.keyPressed(keyCode, scanCode, modifiers)
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