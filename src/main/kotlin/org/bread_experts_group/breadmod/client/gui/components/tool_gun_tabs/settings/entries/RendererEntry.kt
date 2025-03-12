package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import org.bread_experts_group.breadmod.client.gui.components.ModelViewerWidget
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryButton
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.RENDERER
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.registry.item.ModItems

class RendererEntry(screen: ToolGunScreen) : SettingsEntry(
	"renderer",
	RENDERER,
	screen,
	SettingsEntryButton(
		Component.literal("Renderer"),
		Component.literal("Entry for adjusting tool gun rendering parameters."),
		RENDERER
	)
) {
	private val toolGunRenderer = ToolGunItemRenderer()

	override fun init() {
		this.addChild(
			"model_viewer",
			ModelViewerWidget(screen = this.screen) { poseStack, bufferSource ->
				ModelViewerWidget.setupRender(235.0, 30.0, 180.0, poseStack)
				this.toolGunRenderer.renderToolGun(
					ModItems.TOOL_GUN.toStack(),
					FIRST_PERSON_RIGHT_HAND,
					poseStack,
					bufferSource,
					0XFFFFFF,
					OverlayTexture.NO_OVERLAY,
					ToolGunClientGlobals.getCurrentMode(),
					this.toolGunRenderer.helper
				)
			},
			this.x + 125,
			this.y + 5
		)
	}
}