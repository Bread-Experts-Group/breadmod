package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.client.gui.components.ModelViewerWidget
import org.bread_experts_group.breadmod.client.gui.components.ScrollingContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryButton
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.RENDERER
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData

class RendererEntry(
	screen: ToolGunScreen,
	stack: ItemStack
) : SettingsEntry(
	"renderer",
	RENDERER,
	screen,
	stack,
	SettingsEntryButton(
		Component.literal("Renderer"),
		Component.literal("Entry for adjusting tool gun rendering parameters."),
		RENDERER
	)
) {
	private val toolGunRenderer = ToolGunItemRenderer

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderContainer(guiGraphics, mouseX, mouseY, partialTick)
	}

	override fun init() {
		val (_, _, index) = ToolGunData.get(this.stack)
		this.addChild(
			"model_viewer",
			ModelViewerWidget(screen = this.screen) { poseStack, bufferSource ->
				ModelViewerWidget.setupRender(235.0, 30.0, 180.0, poseStack)
				this.toolGunRenderer.renderToolGun(
					this.stack,
					FIRST_PERSON_RIGHT_HAND,
					poseStack,
					bufferSource,
					0XFFFFFF,
					OverlayTexture.NO_OVERLAY,
					toolGunModes.values.elementAt(index)
				)
			},
			this.x + 125,
			this.y + 5
		)
//		this.addChild(
//			"test_list",
//			Test(this.x + 5, this.y + 17)
//		)
		this.addChild(
			"test_scroll_container",
			ScrollingContainerWidget(
				this.x + 5,
				this.y + 17,
				100,
				100,
				"test_container",
				this.screen
			)
		)
	}
//	class Test(x: Int, y: Int) : AbstractScrollWidget(x, y, 100, 100, Component.empty()) {
//		override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
//		}
//
//		override fun getInnerHeight(): Int = 200
//
//		override fun scrollRate(): Double = 10.0
//
//		override fun scrollbarWidth(): Int = 3
//
//		override fun renderContents(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
//			guiGraphics.borderedFillPositioned(this.x, this.y, 100, this.innerHeight, Color.BLACK, Color.GRAY)
//		}
//
//		override fun renderDecorations(guiGraphics: GuiGraphics) {
//			if (this.scrollbarVisible()) this.renderScrollBar(guiGraphics)
//		}
//
//		override fun renderScrollBar(guiGraphics: GuiGraphics) {
//			val height = this.scrollBarHeight
//			val width = this.scrollbarWidth()
//			val x = this.x + this.width - width
//			val y = max(
//				this.y.toDouble(),
//				(this.scrollAmount() * (this.height - height) / this.maxScrollAmount + this.y)
//			).toInt()
//			guiGraphics.borderedFillPositioned(x, y, width, height, Color.DARK_GRAY, Color.BLACK)
//		}
//	}
}