package breadmod.block.entity.screen

import breadmod.block.entity.menu.AbstractPowerGeneratorMenu
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

abstract class AbstractPowerGeneratorScreen<R: FluidEnergyRecipe, T: AbstractPowerGeneratorMenu<R>>(
    pMenu: T,
    pPlayerInventory: Inventory,
    pTitle: Component,
    private val pTexture: ResourceLocation
) : AbstractContainerScreen<T>(pMenu, pPlayerInventory, pTitle) {
    private val textureWidth = 176
    private val textureHeight = 166

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, pTexture)

        pGuiGraphics.blit(pTexture, leftPos, topPos, 0, 0, textureWidth, textureHeight)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}