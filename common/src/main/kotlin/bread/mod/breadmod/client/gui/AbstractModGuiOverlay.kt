package bread.mod.breadmod.client.gui

import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource

abstract class AbstractModGuiOverlay: LayeredDraw.Layer {
    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val gameWindow = rgMinecraft.window ?: return
        val screenWidth = gameWindow.guiScaledWidth
        val screenHeight = gameWindow.guiScaledHeight
        val poseStack = guiGraphics.pose()
        val player = rgMinecraft.player ?: return
        val buffer = guiGraphics.bufferSource()
        val partialTick = deltaTracker.realtimeDeltaTicks

        renderOverlay(guiGraphics, partialTick, deltaTracker, screenWidth, screenHeight, poseStack, buffer, player)
    }

    abstract fun renderOverlay(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        deltaTracker: DeltaTracker,
        screenWidth: Int,
        screenHeight: Int,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        player: LocalPlayer
    )
}