package org.bread_experts_group.breadmod.client.gui

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.client.renderer.texture.DynamicTexture
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import org.bread_experts_group.breadmod.util.render.scaleFlat

class CameraOverlay : LayeredDraw.Layer {
    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val mc = Minecraft.getInstance()
        val renderTarget = Minecraft.getInstance().mainRenderTarget
        val image = Screenshot.takeScreenshot(renderTarget)
        val texture = DynamicTexture(image)
        val resource = rgMinecraft.textureManager.register("test", texture)
        guiGraphics.pose().pushPose()
        guiGraphics.pose().scaleFlat(0.05f)
        guiGraphics.blit(
            resource,
            0,
            0,
            0f,
            0f,
            rgMinecraft.window.width,
            rgMinecraft.window.height,
            renderTarget.width,
            renderTarget.height
        )
        guiGraphics.pose().scaleFlat(1f)
        guiGraphics.pose().popPose()
        mc.textureManager.release(resource)
        image.close()
    }
}