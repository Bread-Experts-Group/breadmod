package org.bread_experts_group.breadmod.client.tool_gun_mode

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper
import java.awt.Color

class ModeWidget(
	/**
	 * Holds all data for this [ModeWidget], used for loading from server to clients.
	 */
	val data: ModeWidgetData
) : AbstractWidget(0, 0, 35, 40, data.modeName) {
	/**
	 * Backup constructor for [ModeWidget].
	 */
	constructor(
		icon: ItemStack,
		previewImage: BreadModTextureHelper,
		modeName: Component,
		modeDescription: Component
	) : this(ModeWidgetData(icon, previewImage, modeName, modeDescription))

//	override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
//		PacketDistributor.sendToServer(ToolGunActionPacket(this.namespace, this.id))
//		super.onClick(mouseX, mouseY, button)
//	}
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.pose().pushPose()
		guiGraphics.fill(
			RenderType.gui(),
			this.x,
			this.y,
			this.x + 35,
			this.y + 40,
			if (this.isHovered || this.isFocused) Color(16755200).rgb else Color.GRAY.rgb
		)
		guiGraphics.fill(RenderType.gui(), this.x + 1, this.y + 1, this.x + 34, this.y + 39, Color.DARK_GRAY.rgb)
		guiGraphics.drawScrollingString(
			localClient.font,
			this.message,
			this.x + 2,
			this.x + 33,
			this.y + 29,
			Color.WHITE.rgb
		)
		guiGraphics.pose().translate(this.x.toFloat() + 5.5f, this.y.toFloat() + 2, 0f)
		guiGraphics.pose().scaleFlat(1.5f)
		guiGraphics.renderFakeItem(this.data.icon, 0, 0)
		guiGraphics.pose().popPose()
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}