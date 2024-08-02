package breadmod.client.screen.tool_gun

import breadmod.ModMain.modTranslatable
import breadmod.ModMain.modLocation
import breadmod.item.tool_gun.mode.ToolGunCreatorMenu
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import java.awt.Color

class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        val TEXTURE = modLocation("textures", "gui", "item", "tool_gun", "creator_mode.png")
    }

    init {
        imageWidth = 256
        imageHeight = 220
    }

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawString(font, title, 2, 2, Color.WHITE.rgb, false)
        pGuiGraphics.drawString(font, playerInventoryTitle, 2, 132, Color.WHITE.rgb, false)
        pGuiGraphics.drawString(font, modTranslatable("tool_gun", "creator", "save_load"), 167, 132, Color.WHITE.rgb, false)
    }
}