package breadmod.block.entity.menu

import breadmod.BreadMod
import breadmod.util.formatUnit
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
        renderEnergyMeter(pGuiGraphics, x, y)
        renderFluidMeter(pGuiGraphics, x, y)
    }

    private fun renderProgressArrow(pGuiGraphics : GuiGraphics, x : Int, y : Int) {
        if(menu.isCrafting()) {
            pGuiGraphics.blit(texture, x + 46, y + 35, 176, 0, menu.getScaledProgress(), 17)
        }
    }

    private fun renderEnergyMeter(pGuiGraphics: GuiGraphics, x: Int, y: Int) {
        val energyStored = menu.getEnergyStoredScaled()
        pGuiGraphics.blit(texture, x + 132, y + 28 + 47 - energyStored, 176, 64 - energyStored, 16, 47)
    }

    private fun renderFluidMeter(pGuiGraphics: GuiGraphics, x: Int, y: Int) {
        val fluidStored = menu.getFluidStoredScaled()
        pGuiGraphics.blit(texture, x + 153, y + 28 + 47 - fluidStored, 192, 64 - fluidStored, 16, 47)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)

        // Power Tooltip
        if(this.isHovering(132,28, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
            val energy = menu.parent.data[2]; val maxEnergy = menu.parent.data[3]
            pGuiGraphics.renderComponentTooltip(this.font, listOf(Component.literal(formatUnit(energy, maxEnergy, "FE", true, 2))), pMouseX, pMouseY)
        }
        // Fluid Tooltip
        if(this.isHovering(153,28, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
            val fluid = menu.parent.data[4]; val maxFluid = menu.parent.data[5]
            pGuiGraphics.renderComponentTooltip(this.font, listOf(Component.literal(formatUnit(fluid, maxFluid, "B", true, 2, -1))), pMouseX, pMouseY)
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, delta)
    }
}