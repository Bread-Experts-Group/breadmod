package breadmod.block.entity.menu

import breadmod.BreadMod
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class DoughMachineScreen(
    pMenu: DoughMachineMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<DoughMachineMenu>(pMenu, pPlayerInventory, pTitle) {
    val texture = ResourceLocation(BreadMod.ID, "textures/gui/container/dough_machine.png")

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F)
        RenderSystem.setShaderTexture(0, texture)

        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        pGuiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight)

        renderProgressArrow(pGuiGraphics, x, y)
    }

    private fun renderProgressArrow(pGuiGraphics : GuiGraphics, x : Int, y : Int) {
        if(menu.isCrafting()) {
            pGuiGraphics.blit(texture, x + 79, y + 35, 176, 0, menu.getScaledProgress(), 17)
        }
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}