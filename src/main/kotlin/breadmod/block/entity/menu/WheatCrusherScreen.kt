package breadmod.block.entity.menu

import breadmod.ModMain.modLocation
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class WheatCrusherScreen(
    pMenu: WheatCrusherMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<WheatCrusherMenu>(pMenu, pPlayerInventory, pTitle) {
    val texture = modLocation("textures", "gui", "container", "wheat_crusher.png")
    private val textureWidth = 176
    private val textureHeight = 198


    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        RenderSystem.setShaderTexture(0, texture)

        pGuiGraphics.blit(texture, leftPos, topPos, 0, 0, textureWidth, textureHeight)
        inventoryLabelY = textureHeight - 94

        renderEnergyMeter(pGuiGraphics)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }

    private fun renderEnergyMeter(pGuiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        pGuiGraphics.blit(texture, leftPos + 133, topPos + 49 + 47 - energyStored, 176, 64 - energyStored, 16, 47)
    }
}