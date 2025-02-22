package org.bread_experts_group.breadmod.client.tool_gun

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderTypeDebugLineStrip
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.tool_gun.SettingsTab.SettingsEntries.MAIN
import org.bread_experts_group.breadmod.client.tool_gun.SettingsTab.SettingsEntries.RENDERER
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.currentModeIndex
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.awt.Color

class SettingsTab(x: Int, y: Int) : ToolGunScreenTab(x, y, "settings", Color(0, 0, 180)) {
	enum class SettingsEntries { MAIN, RENDERER }

	private var currentSettingsEntry: SettingsEntries = MAIN
	private val renderHelper = ToolGunRenderHelper()
	private val toolGunRenderer = ToolGunItemRenderer()

	override fun getTabButton(): TabButton =
		TabButton(Component.literal("settings"), Color.BLUE, Color(0, 0, 230), this)

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
//		guiGraphics.fill(
//			this.x + 7,
//			this.y + 27,
//			this.x + 250,
//			this.y + 38,
//			Color(0, 0, 139, 255).rgb
//		)
		guiGraphics.fill(
			this.x,
			this.y,
			this.x + 243,
			this.y + 185,
			Color(0, 0, 230, 255).rgb
		)
		this.currentSettingsEntry = RENDERER
		when (this.currentSettingsEntry) {
			MAIN     -> {
				guiGraphics.fill(
					this.x + 7,
					this.y + 70,
					this.x + 250,
					this.y + 72,
					Color.WHITE.rgb
				)
				guiGraphics.fill(
					this.width / 2,
					this.y + 72,
					this.width / 2 + 2,
					this.y + 185,
					Color.WHITE.rgb
				)
				guiGraphics.fill(
					this.x + 7,
					this.y + 185,
					this.x + 250,
					this.y + 187,
					Color.WHITE.rgb
				)
				guiGraphics.drawCenteredString(
					localClient.font,
					modTranslatable("tool_gun", "settings", "title"),
					this.width / 2,
					this.y + 50,
					Color.WHITE.rgb
				)
			}
			RENDERER -> {
				// todo buttons for each settings entry, probably work out a cleaner solution for
				//  this currentSettingsEntry stuff
				this.drawSettingsRendererEntry(guiGraphics)
			}
		}
	}

	private fun getCurrentMode() = toolGunModes.values.elementAt(currentModeIndex)

	private fun drawSettingsRendererEntry(guiGraphics: GuiGraphics) {
		val poseStack = guiGraphics.pose()
		val bufferSource = localClient.renderBuffers().bufferSource()
		val stack = ModItems.TOOL_GUN.toStack()
		val gameTime = (localClient.level ?: return).gameTime
		guiGraphics.vLine(this.width / 2, this.y + 20, this.y + 200, Color.WHITE.rgb)
		guiGraphics.borderedFill(
			RenderType.gui(),
			this.x + 150,
			this.y + 50,
			this.x + 220,
			this.y + 120,
			Color.WHITE.rgb,
			Color.BLACK.rgb
		)
		poseStack.pushPose()
		poseStack.translate(this.x + 350.0, this.y + 100.0, 200.0)
		poseStack.translate(-180.0, 0.0, 0.0)
		poseStack.mulPose(Axis.YN.rotationDegrees(gameTime.toFloat() % 360))
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
			this.getCurrentMode(),
			this.renderHelper,
			overrideRenderType = true,
			renderTypeOverride = renderTypeDebugLineStrip()
		)
		RenderSystem.disableBlend()
		poseStack.popPose()
	}
}