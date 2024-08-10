package breadmod.client.screen.tool_gun.creator

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.renderer.RenderType
import java.awt.Color

// todo add parameters for automatically adding resize widgets and adding onto a future potion effects map
// todo add and remove buttons for deleting the potion effect instance (along with removing that instance from entityEffect)
// todo render calls for rendering the mob effect and bg of that effect instance (Gui.java@448)
// todo automatically adding created effect instance into entityEffect with a system in place for not allowing more than one instance of a potion effect

class PotionEffectGuiElement(private val pX: Int, private val pY: Int): Renderable {
    var visible = true

    fun isVisible(): Boolean = visible

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if(isVisible()) {
            pGuiGraphics.fill(RenderType.endPortal(), pX, pY, pX + 100, pY + 50, Color.RED.rgb)
        }
    }
}