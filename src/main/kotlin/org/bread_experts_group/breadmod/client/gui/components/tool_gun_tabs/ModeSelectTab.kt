package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals.currentModeIndex
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import java.awt.Color

class ModeSelectTab(screen: ToolGunScreen) : ToolGunScreenTab("mode_select", Color.GRAY, screen) {
	override fun getTabButton(): TabButton = TabButton(Component.literal("Modes"), Color.RED, Color.GRAY, this)

	private val modeWidgets: MutableList<ModeWidget> = mutableListOf()
	private var currentModeWidget: ModeWidget? = null
	private val modeButton = GenericButton(0, 0, 80, 20, "Change Mode") {
		this.currentModeWidget?.let { widget ->
			PacketDistributor.sendToServer(ToolGunModeChangePacket(widget.id))
			currentModeIndex = toolGunModes.keys.indexOf(widget.id)
		}
		this.updateModeWidgetSelection()
	}

	override fun init() {
		this.currentModeWidget = null
		val gridList = buildList {
			repeat(5) { y ->
				repeat(3) { x ->
					this.add(this@ModeSelectTab.x + 4 + x * 36 to this@ModeSelectTab.y + 3 + y * 45)
				}
			}
		}
		toolGunModes.forEach { (_, mode) ->
			this.modeWidgets.add(mode.getCustomRenderer().getModeWidget())
		}
		this.modeWidgets.forEachIndexed { index, modeWidget ->
			this.addChild("mode_widget_$index", modeWidget, gridList[index].first, gridList[index].second)
		}
		this.addChild("mode_change_button", this.modeButton, this.x + 160, this.y + 162, isActive = false)
		this.updateModeWidgetSelection()
	}

	/**
	 * Update the border color on the widget's mode that is currently active.
	 */
	private fun updateModeWidgetSelection() = this.getWidgets().filterIsInstance<ModeWidget>().forEach {
		it.isSelected = it.id == ToolGunClientGlobals.getCurrentModeID()
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.currentModeWidget = this.screen.focused as? ModeWidget
		this.modeButton.active = this.currentModeWidget != null
		val poseStack = guiGraphics.pose()
		guiGraphics.borderedFill(
			RenderType.gui(),
			this.x,
			this.y,
			this.x + 243,
			this.y + 185,
			Color.RED.rgb,
			Color(150, 150, 150).rgb
		)
		guiGraphics.vLine(this.x + 120, this.y, this.y + 184, Color.RED.rgb)
		// todo move this to the main screen class for the title
//		guiGraphics.drawString(localClient.font, this.title, this.x + 2, this.y + 2, Color.BLACK.rgb, false)
		guiGraphics.fill(
			RenderType.gui(),
			this.x + 123,
			this.y + 5,
			this.x + 240,
			this.y + 72,
			Color.BLACK.rgb
		)
		guiGraphics.drawString(
			localClient.font,
			this.currentModeWidget?.modeName ?: Component.literal("???"),
			this.x + 123,
			this.y + 74,
			Color.BLACK.rgb,
			false
		)
		guiGraphics.hLine(this.x + 120, this.x + 241, this.y + 83, Color.RED.rgb)
		guiGraphics.drawWordWrap(
			localClient.font,
			this.currentModeWidget?.modeDescription ?: Component.literal("???"),
			this.x + 123,
			this.y + 85,
			120,
			Color.BLACK.rgb
		)
		poseStack.pushPose()
		poseStack.translate(this.x + 123.85f, this.y + 6.25f, 0f)
		poseStack.scaleFlat(0.135f)
		this.currentModeWidget?.previewImage?.blitTexture(guiGraphics, 0, 0, uWidth = 854, vHeight = 480)
			?: BreadModTextureHelper.MISSING_TEXTURE.blitTexture(guiGraphics, 0, 0, uWidth = 854, vHeight = 480)
		poseStack.popPose()
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
	}
}