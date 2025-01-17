package org.bread_experts_group.breadmod.client.tool_gun_mode

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.network.serverbound.ToolGunActionPacket
import java.awt.Color

class TestScreen(title: Component) : Screen(title) {
	private val modeWidgets: MutableList<ModeWidget> = mutableListOf()

	init {
		this.modeWidgets.clear()
		CommonNeoForgeEventBus.toolGunModes.forEach { (_, mode) ->
			this.modeWidgets.add(mode.getModeWidget())
		}
	}

	private var leftPos: Int = (this.width - 280) / 2
	private var topPos: Int = (this.height - 210) / 2
	private var gridList: List<Pair<Int, Int>> = listOf()
	private var currentModeWidget: ModeWidget? = null
	private val modeButton = ModeButton(0, 0) {
		this.currentModeWidget?.let { widget ->
			PacketDistributor.sendToServer(ToolGunActionPacket(widget.id))
		}
	}

	override fun isPauseScreen(): Boolean = false
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		val poseStack = guiGraphics.pose()
		poseStack.pushPose()
		ModTextureLocations.FRAME.blitTexture(guiGraphics, this.leftPos, this.topPos)
		guiGraphics.borderedFill(
			RenderType.gui(),
			this.leftPos + 7,
			this.topPos + 27,
			this.leftPos + 250,
			this.topPos + 223,
			Color.RED.rgb,
			Color(150, 150, 150).rgb
		)
		guiGraphics.vLine(this.leftPos + 127, this.topPos + 27, this.topPos + 222, Color.RED.rgb)

		guiGraphics.drawString(localClient.font, this.title, this.leftPos + 2, this.topPos + 2, Color.BLACK.rgb, false)
		if (this.focused is ModeWidget) {
			// todo redo to render when currentModeWidget is populated to keep rendering after the widget is no longer focused
			val widget = this.focused as ModeWidget
			this.currentModeWidget = widget
			guiGraphics.fill(
				RenderType.gui(),
				this.leftPos + 129,
				this.topPos + 30,
				this.leftPos + 246,
				this.topPos + 97,
				Color.BLACK.rgb
			)
			guiGraphics.drawString(
				localClient.font,
				widget.modeName,
				this.leftPos + 129,
				this.topPos + 100,
				Color.BLACK.rgb,
				false
			)
			guiGraphics.hLine(this.leftPos + 128, this.leftPos + 248, this.topPos + 110, Color.RED.rgb)
			guiGraphics.drawWordWrap(
				localClient.font,
				widget.modeDescription,
				this.leftPos + 129,
				this.topPos + 112,
				120,
				Color.BLACK.rgb
			)
			poseStack.translate(this.leftPos + 129.8f, this.topPos + 31f, 0f)
			poseStack.scaleFlat(0.135f)
			widget.previewImage.blitTexture(guiGraphics, 0, 0, width = 854, height = 480)
		} else {
//			guiGraphics.drawWordWrap(
//				localClient.font,
//				Component.literal("Click on any of the modes to display their preview."),
//				this.leftPos + 179,
//				this.topPos + 3,
//				125,
//				Color.WHITE.rgb
//			)
		}
		poseStack.popPose()
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
					this.add(this@TestScreen.leftPos + 10 + x * 36 to this@TestScreen.topPos + 30 + y * 45)
				}
			}
		}
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