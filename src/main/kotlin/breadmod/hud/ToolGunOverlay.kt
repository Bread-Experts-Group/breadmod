package breadmod.hud

import breadmod.ModMain.modLocation
import breadmod.item.ToolGunItem
import breadmod.registry.item.ModItems
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay
import java.awt.Color

class ToolGunOverlay: IGuiOverlay {
    private val overlayTexture = modLocation("textures", "gui", "hud", "tool_gun_overlay.png")

    override fun render(
        pGui: ForgeGui,
        pGuiGraphics: GuiGraphics,
        pPartialTick: Float,
        pScreenWidth: Int,
        pScreenHeight: Int
    ) {
        val pose = pGuiGraphics.pose()
        val x = pScreenWidth - (pScreenWidth - 3)
        val y = pScreenHeight - (pScreenHeight - 3)
        val player = pGui.minecraft.player
        val holdingToolGun = player?.isHolding(ModItems.TOOL_GUN.get())
        val toolGunMode = ToolGunItem.currentMode


        if(!pGui.minecraft.options.hideGui && holdingToolGun == true) {
//            printOverlayStats(pScreenWidth, pScreenHeight, x, y)

            pGui.setupOverlayRenderState(true, false)
            pose.pushPose()
//            pose.translate(0.0,0.0, -50.0)
            pGuiGraphics.blit(overlayTexture, x, y, 0, 0, 148, 42)
            pose.scale(3.0f, 3.0f, 3.0f)
            pGuiGraphics.drawString(
                pGui.minecraft.font,
                Component.literal("Remover").withStyle(ChatFormatting.BOLD),
                x - 1,
                y,
                Color(255,255,255).rgb,
                false
            )
            pose.popPose()
            pose.pushPose()
            pGuiGraphics.blit(overlayTexture, x, y + 32, 148, 0, 8, 8)
            pose.scale(0.8f, 0.8f, 0.8f)
            pGuiGraphics.drawString(
                pGui.minecraft.font,
                Component.literal("Remove entities with right click."),
                x + 12,
                y + 43,
                Color(255,255,255).rgb,
                false
            )
            pose.popPose()
        }
    }

    private fun printOverlayStats(pScreenWidth: Int, pScreenHeight: Int, x: Int, y: Int) {
        println("-----------------")
        println("Overlay Stats")
        println("screen width: $pScreenWidth")
        println("screen height: $pScreenHeight")
        println("x value: $x")
        println("y value: $y")
        println("-----------------")
    }
}