package bread.mod.breadmod.registry

import bread.mod.breadmod.client.gui.AbstractModGuiOverlay
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.event.events.client.ClientGuiEvent
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

object ClientEvents {
    fun registerOverlays() {
        registerOverlay(WarOverlay())
    }

    // todo some way to register overlays on a certain z-level?
    //  seems a bit unorthodox to translate by 3600 on the z axis to render above everything
    private fun registerOverlay(overlay: AbstractModGuiOverlay) =
        ClientGuiEvent.RENDER_HUD.register(OverlayWrapper(overlay))

    private class OverlayWrapper(
        val overlay: AbstractModGuiOverlay
    ) : ClientGuiEvent.RenderHud {
        override fun renderHud(
            graphics: GuiGraphics,
            deltaTracker: DeltaTracker
        ) {
            val gameWindow = rgMinecraft.window ?: return
            val screenWidth = gameWindow.guiScaledWidth
            val screenHeight = gameWindow.guiScaledHeight
            val poseStack = graphics.pose()
            val player = rgMinecraft.player ?: return
            val buffer = graphics.bufferSource()
            val partialTick = deltaTracker.realtimeDeltaTicks

            overlay.renderOverlay(graphics, partialTick, deltaTracker, screenWidth, screenHeight, poseStack, buffer, player)
        }
    }
}