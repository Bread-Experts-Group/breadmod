package org.bread_experts_group.breadmod.client.gui.screens
//class KeyboardScreen(private val monitorPos: BlockPos) : Screen(Component.empty()) {
//	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
//		val entity = localClient.level!!.getBlockEntity(this.monitorPos) as MonitorBlockEntity
//		entity.computer.keyboard.write(keyCode.toUByte())
//		PacketDistributor.sendToServer(ComputerKeystrokePacket(this.monitorPos, keyCode))
//		return super.keyPressed(keyCode, scanCode, modifiers)
//	}
//
//	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
//		guiGraphics.drawString(
//			localClient.font,
//			Component.literal("Keyboard capture active.").withStyle(ChatFormatting.GOLD),
//			10, 10, Color.WHITE.rgb
//		)
//	}
//
//	override fun isPauseScreen(): Boolean = false
//}