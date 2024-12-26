package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

// todo codec for this widget
abstract class ModeWidget(
	val title : Component,
	val icon : ItemStack,
	val previewImage : ResourceLocation,
	val modeName : Component,
	val modeDescription : Component
) : AbstractWidget(0, 0, 35, 40, title) {
	override fun renderWidget(guiGraphics : GuiGraphics, mouseX : Int, mouseY : Int, partialTick : Float) {
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
		guiGraphics.renderFakeItem(this.icon, 0, 0)
		guiGraphics.pose().popPose()
	}

	override fun updateWidgetNarration(narrationElementOutput : NarrationElementOutput) {
	}
}