package breadmod.block.entity.menu

import breadmod.BreadMod.modLocation
import breadmod.util.formatUnit
import breadmod.util.renderFluid
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.KeyboardInput
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class DoughMachineScreen(
    pMenu: DoughMachineMenu,
    pPlayerInventory: Inventory,
    pTitle: Component,
) : AbstractContainerScreen<DoughMachineMenu>(pMenu, pPlayerInventory, pTitle) {
    val texture = modLocation("textures", "gui", "container", "dough_machine.png")

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F)
        RenderSystem.setShaderTexture(0, texture)

        pGuiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        renderProgressArrow(pGuiGraphics)
        renderEnergyMeter(pGuiGraphics)
    }

    private fun renderProgressArrow(pGuiGraphics: GuiGraphics) {
        if(menu.isCrafting())
            pGuiGraphics.blit(texture, leftPos + 46, topPos + 35, 176, 0, menu.getScaledProgress(), 17)
    }

    private fun renderEnergyMeter(pGuiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        pGuiGraphics.blit(texture, leftPos + 132, topPos + 28 + 47 - energyStored, 176, 64 - energyStored, 16, 47)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)

        val showShort = !KeyboardInput(minecraft!!.options).shiftKeyDown
        menu.parent.fluidHandlerOptional.ifPresent {
            it.tanks.firstOrNull()?.let { tank ->
                if(tank.fluidAmount > 0) {
                    val percentage = (tank.fluidAmount.toFloat() / tank.capacity) * 47
                    pGuiGraphics.renderFluid(
                        pX         = leftPos + 153F,
                        pY         = (topPos + 75F),
                        pWidth     = 16,
                        pHeight    = percentage.toInt(),
                        pFluid     = tank.fluid.fluid,
                        pFlowing   = false,
                        pDirection = Direction.SOUTH
                    )
                }

                if(this.isHovering(153,28, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
                    pGuiGraphics.renderComponentTooltip(
                        this.font,
                        listOf(Component.literal(formatUnit(tank.fluidAmount, tank.capacity, "B", showShort, 2, -1))),
                        pMouseX, pMouseY
                    )
                }
            }
        }

        if(this.isHovering(132,28, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
            menu.parent.energyHandlerOptional.ifPresent {
                pGuiGraphics.renderComponentTooltip(
                    this.font,
                    listOf(Component.literal(formatUnit(it.energyStored, it.maxEnergyStored, "FE", showShort, 2))),
                    pMouseX, pMouseY
                )
            }
        }

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}