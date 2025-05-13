package org.bread_experts_group.breadmod.tool_gun.gui

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.ChatFormatting.BLUE
import net.minecraft.ChatFormatting.BOLD
import net.minecraft.ChatFormatting.ITALIC
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.ModTextureLocations.INFO
import org.bread_experts_group.breadmod.client.ModTextureLocations.MODE_OVERLAY_BG
import org.bread_experts_group.breadmod.client.render.drawScaledText
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import java.awt.Color

class ToolGunOverlay : LayeredDraw.Layer {
	private val textColor: Int = Color.WHITE.rgb
	override fun render(
		guiGraphics: GuiGraphics,
		deltaTracker: DeltaTracker
	) {
		val poseStack = guiGraphics.pose()
		val screenWidth = localClient.window.screenWidth
		val screenHeight = localClient.window.screenHeight
		val x = screenWidth - (screenWidth - 3)
		val y = screenHeight - (screenHeight - 3)
		val handStack = getStackInPlayerHand(localClient.player)

		if (!localClient.options.hideGui && handStack.item is ToolGunItem) {
			val data = ToolGunData.get(handStack)
			RenderSystem.enableBlend()
			this.renderBackground(guiGraphics, poseStack, x, y)
			this.renderMode(data, guiGraphics, poseStack, deltaTracker, handStack, x, y)

			RenderSystem.disableBlend()
		}
	}

	private fun renderBackground(guiGraphics: GuiGraphics, poseStack: PoseStack, x: Int, y: Int) {
		poseStack.pushPose()
		MODE_OVERLAY_BG.blitTexture(guiGraphics, x, y)
		poseStack.popPose()
	}

	private fun renderMode(
		data: ToolGunData,
		guiGraphics: GuiGraphics,
		poseStack: PoseStack,
		deltaTracker: DeltaTracker,
		stack: ItemStack,
		x: Int,
		y: Int
	) {
		val mode = data.mode
		poseStack.pushPose()
		// Icon renders
		INFO.blitTexture(guiGraphics, x + 1, y + 33)
		// start rendering key (with the key letter on them) and mouse icons, fix positioning on description and
		// controls, set up 9-sliced key texture for wider keys
		// Action source
		drawScaledText(
			Component.literal(mode.getUid().namespace).withStyle(BLUE, ITALIC),
			poseStack, guiGraphics, x + 2, y + 2, this.textColor, 0.8f, true
		)
		// Action Name
		drawScaledText(
			mode.getDisplayName().copy().withStyle(BOLD),
			poseStack, guiGraphics, x - 1, y + 4, this.textColor, 2.5f, false
		)
		// Mode Tooltip
		drawScaledText(
			mode.getTooltip().copy(),
			poseStack,
			guiGraphics,
			x + 13,
			y + 43,
			this.textColor,
			0.4f,
			true
		)
		var offset = 0
		data.keyData.forEach { (keyId, data) ->
			val key = InputConstants.Type.KEYSYM.getOrCreate(keyId)
			drawScaledText(
				key.displayName.copy()
					.withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD)
					.append("... ")
					.append(data.first.copy().withStyle(ChatFormatting.WHITE)),
				poseStack,
				guiGraphics,
				x + 10,
				y + 54 + offset,
				this.textColor,
				1f,
				true
			)
			offset += 12
		}
		mode.getCustomRenderer().renderOverlayAdditions(guiGraphics, deltaTracker, stack, data)
		// KeyBinds
//        mode?.keyBinds?.forEachIndexed { index, control ->
//            val moved = ((index + 1) * 12) + 2
//            drawScaledText(
//                toolGunBindList[control]!!.translatedKeyMessage.copy()
//                    .withStyle { it.withColor(ChatFormatting.GOLD).withItalic(true) }
//                    .append(control.toolGunComponent.copy().withStyle(ChatFormatting.WHITE)),
//                poseStack, guiGraphics,
//                pX + 10, pY + 43 + moved, textColor, 1f, true
//            )
//            toolGunBindList[control]?.key = InputConstants.getKey("key.mouse.right")
//            when (toolGunBindList[control]?.key) {
//                getInput("key.mouse.right") -> {
//                    poseStack.pushPose()
//                    poseStack.scaleFlat(0.68f)
//                    guiGraphics.blit(overlayTexture, pX, pY + 69 + moved, 240, 63, 16, 16)
//                    poseStack.popPose()
//                }
//
//                getInput("key.mouse.middle") -> {
//                    poseStack.pushPose()
//                    poseStack.scaleFlat(0.68f)
//                    guiGraphics.blit(overlayTexture, pX, pY + 73 + moved, 240, 47, 16, 16)
//                    poseStack.popPose()
//                }
//            }
//        }
		poseStack.popPose()
	}
}