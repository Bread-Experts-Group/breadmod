package org.bread_experts_group.breadmod.client.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.toolGunModesClient
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import java.awt.Color

class ToolGunScreen(title: Component) : Screen(title) {
	private val modeWidgets: MutableList<ModeWidget> = mutableListOf()

	init {
		this.modeWidgets.clear()
		toolGunModesClient.forEach { (_, mode) ->
			this.modeWidgets.add(mode.getModeWidget())
		}
	}

	private var leftPos: Int = (this.width - 280) / 2
	private var topPos: Int = (this.height - 210) / 2
	private var gridList: List<Pair<Int, Int>> = listOf()
	private var currentModeWidget: ModeWidget? = null
	private val modeButton = ModeButton(0, 0) {
		this.currentModeWidget?.let { widget ->
			PacketDistributor.sendToServer(ToolGunModeChangePacket(widget.id))
			ToolGunClientGlobals.currentMode = toolGunModesClient[widget.id]
		}
	}

	override fun isPauseScreen(): Boolean = false
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		ModTextureLocations.FRAME.blitTexture(guiGraphics, this.leftPos, this.topPos)
		if (this.focused is ModeWidget) this.currentModeWidget = this.focused as ModeWidget
		this.renderModeTab(guiGraphics)
	}

	private fun renderModeTab(guiGraphics: GuiGraphics) {
		val poseStack = guiGraphics.pose()
		guiGraphics.borderedFill(
			RenderType.gui(),
			this.leftPos + 7,
			this.topPos + 38,
			this.leftPos + 250,
			this.topPos + 223,
			Color.RED.rgb,
			Color(150, 150, 150).rgb
		)
		guiGraphics.vLine(this.leftPos + 127, this.topPos + 37, this.topPos + 222, Color.RED.rgb)

		guiGraphics.drawString(localClient.font, this.title, this.leftPos + 2, this.topPos + 2, Color.BLACK.rgb, false)
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 129,
			this.topPos + 40,
			this.leftPos + 246,
			this.topPos + 107,
			Color.BLACK.rgb
		)
		guiGraphics.drawString(
			localClient.font,
			this.currentModeWidget?.modeName ?: Component.literal("???"),
			this.leftPos + 129,
			this.topPos + 109,
			Color.BLACK.rgb,
			false
		)
		guiGraphics.hLine(this.leftPos + 128, this.leftPos + 248, this.topPos + 118, Color.RED.rgb)
		guiGraphics.drawWordWrap(
			localClient.font,
			this.currentModeWidget?.modeDescription ?: Component.literal("???"),
			this.leftPos + 129,
			this.topPos + 121,
			120,
			Color.BLACK.rgb
		)
		poseStack.pushPose()
		poseStack.translate(this.leftPos + 129.85f, this.topPos + 41.25f, 0f)
		poseStack.scaleFlat(0.135f)
		this.currentModeWidget?.previewImage?.blitTexture(guiGraphics, 0, 0, width = 854, height = 480)
			?: BreadModTextureHelper.MISSING_TEXTURE.blitTexture(guiGraphics, 0, 0, width = 854, height = 480)
		poseStack.popPose()
	}

	private fun renderSettingsTab(guiGraphics: GuiGraphics) {
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		if (keyCode == InputConstants.KEY_E) {
			this.onClose()
			true
		} else super.keyPressed(keyCode, scanCode, modifiers)

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2
		this.gridList = buildList {
			repeat(5) { y ->
				repeat(3) { x ->
					this.add(this@ToolGunScreen.leftPos + 10 + x * 36 to this@ToolGunScreen.topPos + 40 + y * 45)
				}
			}
		}
		// todo make a "screen" button that opens a screen from the selected mode if one is present
		//  alternative: when the tabs on the top of the tool gun screen eventually exist we can have a new tab appear
		//  when the selected mode has a valid screen.
		this.addRenderableWidget(this.modeButton.also { it.setPosition(this.leftPos + 155, this.topPos + 190) })

		repeat(this.modeWidgets.size) { index ->
			this.modeWidgets[index].x = this.gridList[index].first
			this.modeWidgets[index].y = this.gridList[index].second
		}
		this.modeWidgets.forEach(this::addRenderableWidget)
	}

	private class ModeButton(x: Int, y: Int, onPress: OnPress) :
		Button(x, y, 80, 20, Component.literal("Change Mode"), onPress, { Component.empty() })
}