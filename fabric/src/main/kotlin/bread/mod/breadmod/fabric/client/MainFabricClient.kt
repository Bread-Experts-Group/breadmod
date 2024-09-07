package bread.mod.breadmod.fabric.client

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.AbstractModGuiOverlay
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.util.render.rgMinecraft
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

class MainFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        registerOverlay(modLocation("war_timer_overlay"), WarOverlay())
    }

    private fun registerOverlay(id: ResourceLocation, overlay: AbstractModGuiOverlay) =
        HudRenderCallback.EVENT.register(id, OverlayWrapper(overlay))

    private class OverlayWrapper(
        val overlay: AbstractModGuiOverlay
    ): HudRenderCallback {
        override fun onHudRender(
            drawContext: GuiGraphics,
            tickCounter: DeltaTracker
        ) {
            val gameWindow = rgMinecraft.window ?: return
            val screenWidth = gameWindow.guiScaledWidth
            val screenHeight = gameWindow.guiScaledHeight
            val poseStack = drawContext.pose()
            val player = rgMinecraft.player ?: return
            val buffer = drawContext.bufferSource()
            val partialTick = tickCounter.realtimeDeltaTicks

            overlay.renderOverlay(drawContext, partialTick, tickCounter, screenWidth, screenHeight, poseStack, buffer, player)
        }
    }
}
