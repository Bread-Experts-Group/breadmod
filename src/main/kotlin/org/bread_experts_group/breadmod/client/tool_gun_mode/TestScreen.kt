package org.bread_experts_group.breadmod.client.tool_gun_mode

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.network.serverbound.ToolGunActionPacket
import java.awt.Color

// todo proof of concept
//  needs proper gui centering, and actual logic for putting together selectable modes and previews
class TestScreen(title: Component) : Screen(title) {
	companion object {
		val modeWidgets: MutableList<ModeWidget> = mutableListOf()
	}

	init {
		Companion.modeWidgets.clear()
		ToolGunModeDataLoader.modes.forEach { (_, u) ->
			u.forEach { (_, data) ->
				Companion.modeWidgets.add(ModeWidget.fromData(data.widgetData))
			}
		}
	}

	private var leftPos: Int = (this.width - 280) / 2
	private var topPos: Int = (this.height - 210) / 2
	private var gridList: List<Pair<Int, Int>> = listOf()
	private var currentModeWidget: ModeWidget? = null

	override fun isPauseScreen(): Boolean = false
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		val poseStack = guiGraphics.pose()
		poseStack.pushPose()
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos,
			this.topPos,
			this.leftPos + 173,
			this.topPos + 200,
			Color.RED.rgb
		)
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 1,
			this.topPos + 1,
			this.leftPos + 172,
			this.topPos + 199,
			Color(150, 150, 150).rgb
		)

		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 175,
			this.topPos,
			this.leftPos + 300,
			this.topPos + 200,
			Color.RED.rgb
		)
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 176,
			this.topPos + 1,
			this.leftPos + 299,
			this.topPos + 199,
			Color(150, 150, 150).rgb
		)

		guiGraphics.drawString(localClient.font, this.title, this.leftPos + 2, this.topPos + 2, Color.BLACK.rgb, false)
		if (this.focused is ModeWidget) {
			val widget = this.focused as ModeWidget
			this.currentModeWidget = widget
			guiGraphics.fill(
				RenderType.gui(),
				this.leftPos + 179,
				this.topPos + 3,
				this.leftPos + 296,
				this.topPos + 70,
				Color.BLACK.rgb
			)
			guiGraphics.drawString(
				localClient.font,
				widget.modeName,
				this.leftPos + 179,
				this.topPos + 73,
				Color.BLACK.rgb,
				false
			)
			guiGraphics.fill(
				RenderType.gui(),
				this.leftPos + 176,
				this.topPos + 83,
				this.leftPos + 299,
				this.topPos + 84,
				Color.RED.rgb
			)
			guiGraphics.drawWordWrap(
				localClient.font,
				widget.modeDescription,
				this.leftPos + 179,
				this.topPos + 86,
				120,
				Color.BLACK.rgb
			)
			poseStack.translate(this.leftPos + 179.8f, this.topPos + 4f, 0f)
			poseStack.scaleFlat(0.135f)
			widget.previewImage.blitTexture(guiGraphics, 0, 0, width = 854, height = 480)
		} else {
			guiGraphics.drawWordWrap(
				localClient.font,
				Component.literal("Click on any of the modes to display their preview."),
				this.leftPos + 179,
				this.topPos + 3,
				125,
				Color.WHITE.rgb
			)
		}
		poseStack.popPose()
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		if (keyCode == InputConstants.KEY_E) {
			this.onClose()
			true
		} else super.keyPressed(keyCode, scanCode, modifiers)

	override fun init() {
		this.leftPos = (this.width - 280) / 2
		this.topPos = (this.height - 210) / 2
		this.gridList = buildList {
			repeat(5) { y ->
				repeat(4) { x ->
					this.add(this@TestScreen.leftPos + 10 + x * 40 to this@TestScreen.topPos + 15 + y * 45)
				}
			}
		}
		this.addRenderableWidget(ModeButton(this.leftPos + 40, this.topPos + 80) {
			this.currentModeWidget?.let { widget ->
				PacketDistributor.sendToServer(ToolGunActionPacket(widget.namespace, widget.id))
			}
		})

		repeat(Companion.modeWidgets.size) { index ->
			Companion.modeWidgets[index].x = this.gridList[index].first
			Companion.modeWidgets[index].y = this.gridList[index].second
		}
		Companion.modeWidgets.forEach(this::addRenderableWidget)
	}

	private class ModeButton(x: Int, y: Int, onPress: OnPress) :
		Button(x, y, 30, 15, Component.literal("funny"), onPress, { Component.empty() })
}