package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.ModelViewerWidget
import org.bread_experts_group.breadmod.client.gui.components.ScrollingContainerWidget
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.tool_gun.gui.components.SettingsEntryButton
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.RENDERER
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
	private val toolGunRenderer: ToolGunItemRenderer = ToolGunItemRenderer

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
					Registry.toolGunModes.values.elementAt(index)
				)
			},
			this.x + 125,
			this.y + 5
		)
		Registry.toolGunModes.forEach { (_, mode) ->
			val renderer = mode.getCustomRenderer()
			if (renderer is EmptyMode.EmptyModeRenderer) return@forEach
		}
		// todo setup widgets for each renderer
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
				Color.WHITE,
				Color.DARK_GRAY
			) { container ->
				repeat(20) { repeat ->
					container.addChild(
						"test_$repeat",
						GenericButton(0, 0, 80, 15, "$repeat") {
							LogManager.getLogger().info(repeat)
						},
						container.x,
						container.y + (repeat * 18)
					)
				}
			}
		)
	}
}