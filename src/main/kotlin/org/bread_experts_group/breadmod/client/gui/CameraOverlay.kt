package org.bread_experts_group.breadmod.client.gui

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.client.renderer.texture.DynamicTexture
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

class CameraOverlay : LayeredDraw.Layer {
	override fun render(guiGraphics : GuiGraphics, deltaTracker : DeltaTracker) {
		val mc = Minecraft.getInstance()
		val player = mc.player ?: return
		val stack = if (!player.mainHandItem.isEmpty) player.mainHandItem else player.offhandItem
		if (!stack.`is`(ModItems.TEST_BREAD)) return
		val renderTarget = Minecraft.getInstance().mainRenderTarget
		val image = Screenshot.takeScreenshot(renderTarget)
		val texture = DynamicTexture(image)
		val resource = localClient.textureManager.register("test", texture)
		guiGraphics.pose().pushPose()
		guiGraphics.drawString(localClient.font, "LIVE DEV REACTION", 0, 0, Color.WHITE.rgb)
		guiGraphics.pose().scaleFlat(0.05f)
		guiGraphics.blit(
			resource,
			0,
			0,
			0f,
			0f,
			localClient.window.width,
			localClient.window.height,
			renderTarget.width,
			renderTarget.height
		)
		guiGraphics.pose().scaleFlat(1f)
		guiGraphics.pose().popPose()
		mc.textureManager.release(resource)
		image.close()
	}
}