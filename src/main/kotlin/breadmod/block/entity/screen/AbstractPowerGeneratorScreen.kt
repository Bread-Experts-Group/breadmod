package breadmod.block.entity.screen

import breadmod.block.entity.menu.AbstractPowerGeneratorMenu
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.ToggleMachinePacket
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
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

        renderEnergyMeter(pGuiGraphics)
        renderRecipeProgress(pGuiGraphics)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }

    open fun renderEnergyMeter(pGuiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        pGuiGraphics.blit(pTexture, leftPos + 108, topPos + 19 + 47 - energyStored, 176, 47 - energyStored, 16, 47)
    }

    open fun renderRecipeProgress(pGuiGraphics: GuiGraphics) {
        if(menu.isCrafting()) {
            val progress = menu.getScaledProgress()
            pGuiGraphics.blit(pTexture, leftPos + 53, topPos + 53 + progress, 192, progress, 14, 14)
        }
    }

    override fun init() {
        super.init()
        addRenderableWidget(ToggleMachineButton(9, 121, 27, false))
        addRenderableWidget(ToggleMachineButton(9, 121, 37, true))
    }

    inner class ToggleMachineButton(pSize: Int, pX: Int, pY: Int, private val toggle: Boolean): AbstractButton(leftPos + pX, topPos + pY, pSize, pSize, Component.empty()) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            NETWORK.sendToServer(ToggleMachinePacket(menu.parent.blockPos, toggle))
        }
    }
}