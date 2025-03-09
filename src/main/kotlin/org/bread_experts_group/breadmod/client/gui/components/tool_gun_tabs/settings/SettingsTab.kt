package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.drawCenteredWordWrap
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderTypeDebugLineStrip
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntries.MAIN
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntries.RENDERER
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.client.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ToolGunScreenTab
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.awt.Color

class SettingsTab(screen: ToolGunScreen) : ToolGunScreenTab("settings", Color(0, 0, 180), screen) {
	companion object {
		var currentSettingsEntry: SettingsEntries = MAIN
	}

	private val renderHelper = ToolGunRenderHelper()
	private val toolGunRenderer = ToolGunItemRenderer()
	private val deltaTracker = localClient.timer
	private val partialTick = this.deltaTracker.gameTimeDeltaTicks

	override fun getTabButton(): TabButton =
		TabButton(Component.literal("settings"), Color.BLUE, Color(0, 0, 230), this)

	override fun init() {
		Companion.currentSettingsEntry = MAIN
		this.addChild(
			"renderer_entry",
			SettingsEntryButton(
				Component.literal("Renderer"),
				Component.literal("Entry for adjusting tool gun rendering parameters."),
				RENDERER
			),
			this.x + 5,
			this.y + 30
		)
		this.addChild(
			"main_button",
			SettingsEntryButton(Component.literal("<"), Component.empty(), MAIN),
			this.x + 5,
			this.y + 5
		)
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(
			this.x,
			this.y,
			this.x + 243,
			this.y + 185,
			Color(0, 0, 230, 255).rgb
		)
		this.getWidgets().filterIsInstance<SettingsEntryButton>()
			.asSequence()
			.filter { it.isHovered && it.visible && Companion.currentSettingsEntry == MAIN }
			.forEach {
				guiGraphics.drawCenteredWordWrap(
					localClient.font,
					it.description,
					this.screen.width / 2 + 133,
					this.y + 150,
					260,
					Color.WHITE.rgb
				)
			}

		this.entryButtonsVisibility(Companion.currentSettingsEntry == MAIN)
		when (Companion.currentSettingsEntry) {
			MAIN     -> this.drawMainEntry(guiGraphics)
			RENDERER -> this.drawSettingsRendererEntry(guiGraphics)
		}
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
	}

	private fun entryButtonsVisibility(visible: Boolean) {
		(this.getChild("renderer_entry") ?: return).let { it.visible = visible; it.active = visible }
		(this.getChild("main_button") ?: return).let { it.active = !visible; it.visible = !visible }
	}

	private fun drawMainEntry(guiGraphics: GuiGraphics) {
		guiGraphics.fill(
			this.x,
			this.y + 22,
			this.x + 243,
			this.y + 24,
			Color.WHITE.rgb
		)
		guiGraphics.fill(
			this.screen.width / 2,
			this.y + 22,
			this.screen.width / 2 + 2,
			this.y + 135,
			Color.WHITE.rgb
		)
		guiGraphics.fill(
			this.x,
			this.y + 135,
			this.x + 243,
			this.y + 137,
			Color.WHITE.rgb
		)
		guiGraphics.drawCenteredString(
			localClient.font,
			modTranslatable("tool_gun", "settings", "title"),
			this.screen.width / 2,
			this.y + 7,
			Color.WHITE.rgb
		)
	}

	private var rotation = 0f
	private fun drawSettingsRendererEntry(guiGraphics: GuiGraphics) {
		val poseStack = guiGraphics.pose()
		val bufferSource = localClient.renderBuffers().bufferSource()
		val stack = ModItems.TOOL_GUN.toStack()
		val gameTime = (localClient.level ?: return).gameTime
		guiGraphics.vLine(this.screen.width / 2, this.y, this.y + 185, Color.WHITE.rgb)
//		guiGraphics.borderedFill(
//			RenderType.gui(),
//			this.x + 150,
//			this.y + 50,
//			this.x + 220,
//			this.y + 120,
//			Color.WHITE.rgb,
//			Color.BLACK.rgb
//		)
		guiGraphics.borderedFillPositioned(this.x + 125, this.y + 5, 115, 115, Color.WHITE, Color.BLACK)
		poseStack.pushPose()
		poseStack.translate(this.x + 350.0, this.y + 100.0, 200.0)
		poseStack.translate(-180.0, 0.0, 0.0)
		if (this.rotation >= 360f) this.rotation = 0f
		this.rotation += 2f * this.partialTick
		poseStack.mulPose(Axis.YN.rotationDegrees(this.rotation))
		poseStack.mulPose(Axis.ZP.rotationDegrees(10f))
		poseStack.translate(180.0, 0.0, 0.0)
		poseStack.scaleFlat(-180.0f)
		RenderSystem.enableBlend()
		this.toolGunRenderer.renderToolGun(
			stack,
			FIRST_PERSON_RIGHT_HAND,
			poseStack,
			bufferSource,
			0XFFFFFF,
			OverlayTexture.NO_OVERLAY,
			ToolGunClientGlobals.getCurrentMode(),
			this.renderHelper,
			overrideRenderType = true,
			renderTypeOverride = renderTypeDebugLineStrip()
		)
		RenderSystem.disableBlend()
		poseStack.popPose()
	}
}