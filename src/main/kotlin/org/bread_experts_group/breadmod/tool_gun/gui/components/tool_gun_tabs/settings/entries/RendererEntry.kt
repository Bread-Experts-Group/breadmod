package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.ModelViewerWidget
import org.bread_experts_group.breadmod.client.gui.components.ScrollingContainerWidget
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.tool_gun.gui.components.SettingsEntryButton
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum.RENDERER
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen
import org.bread_experts_group.breadmod.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.Color

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
	private var index = 0

	override fun initContainer() {
		this.addChild(
			"model_viewer",
			ModelViewerWidget(screen = this.screen) { modelViewer, poseStack, bufferSource ->
				ModelViewerWidget.setupRender(modelViewer, 0.0, -10.0, 180.0, poseStack)
				ToolGunItemRenderer.renderToolGun(
					this.stack,
					FIRST_PERSON_RIGHT_HAND,
					poseStack,
					bufferSource,
					0XFFFFFF,
					OverlayTexture.NO_OVERLAY,
					Registry.toolGunModes.values.elementAt(this.index)
				)
			},
			this.x + 125,
			this.y + 5
		)

		this.addChild(
			"test_scroll_container",
			ScrollingContainerWidget(
				this.x + 5,
				this.y + 17,
				100,
				140,
				"test_container",
				this.screen,
				100,
				Color.color(198, 198, 198),
				Color.DARK_GRAY
			) { container ->
				var offset = 0
				Registry.toolGunModes.forEach { (_, mode) ->
					val renderer = mode.getCustomRenderer()
					if (renderer is EmptyMode.EmptyModeRenderer) return@forEach
					container.addChild(
						"renderer_${mode.getModeName()}",
						GenericButton(0, 0, 80, 15, mode.getDisplayName()) {
							this.index = Registry.toolGunModes.values.indexOf(mode)
//							ToolGunItemRenderer.rendererOverride = renderer
						},
						container.x + 10,
						container.y + 5 + offset
					)
					offset += 18
				}
			}
		)
	}
}