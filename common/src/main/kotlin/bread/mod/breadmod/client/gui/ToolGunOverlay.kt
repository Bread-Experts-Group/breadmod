package bread.mod.breadmod.client.gui

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import java.awt.Color

@Suppress("unused") // i stg inspection tool if you delete this
class ToolGunOverlay : LayeredDraw.Layer {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")
    private val textColor = Color.WHITE.rgb

    override fun render(
        guiGraphics: GuiGraphics,
        deltaTracker: DeltaTracker
    ) {
        TODO("Not yet implemented")
    }
}