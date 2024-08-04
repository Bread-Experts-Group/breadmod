package breadmod.client.screen

import breadmod.ModMain.modLocation
import breadmod.menu.item.CertificateMenu
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class CertificateItemScreen(
    pMenu: CertificateMenu,
    pPlayerInventory: Inventory,
    pTitle: Component,
) : AbstractContainerScreen<CertificateMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        val TEXTURE = modLocation("textures", "gui", "item", "certificate.png")
    }

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F)
        RenderSystem.setShaderTexture(0, TEXTURE)
        titleLabelY = 3
        inventoryLabelY = 999
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}