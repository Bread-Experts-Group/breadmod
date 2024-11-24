package org.bread_experts_group.breadmod.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.util.formatUnit
import org.bread_experts_group.breadmod.util.renderFluid

class DoughMachineScreen(
    menu: DoughMachineMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<DoughMachineMenu>(menu, inventory, title) {
    val texture = modLocation("textures", "gui", "container", "dough_machine.png")

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        RenderSystem.setShaderTexture(0, texture)

        guiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        renderProgressArrow(guiGraphics)
        renderEnergyMeter(guiGraphics)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val showShort = !(minecraft ?: return).options.keyShift.isDown
        if (this.isHovering(132, 28, 16, 47, mouseX.toDouble(), mouseY.toDouble())) {
            menu.getEnergyHandler()?.let {
                guiGraphics.renderComponentTooltip(
                    font,
                    listOf(
                        modTranslatable(path = arrayOf("energy"))
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.ITALIC),
                        Component.literal(
                            formatUnit(
                                it.energyStored.toDouble(),
                                it.maxEnergyStored.toDouble(),
                                "FE",
                                showShort,
                                2
                            )
                        )
                    ),
                    mouseX, mouseY
                )
            }
        }

        menu.parent.level?.getCapability(Capabilities.FluidHandler.BLOCK, menu.parent.blockPos, menu.parent.horizontal)
            ?.let { handler ->
                handler.getFluidInTank(0).let { tank ->
                    val fluid = tank.fluid
                    if (tank.amount > 0) {
                        val percentage = (tank.amount.toFloat() / handler.getTankCapacity(0)) * 28
                        guiGraphics.renderFluid(
                            x = leftPos + 153F,
                            y = (topPos + 75F),
                            width = 16,
                            height = percentage.toInt(),
                            fluid = fluid,
                            flowing = false,
                            direction = Direction.SOUTH
                        )
                    }
                }
            }

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    private fun renderProgressArrow(guiGraphics: GuiGraphics) {
        if (menu.isCrafting()) {
            guiGraphics.blit(texture, leftPos + 46, topPos + 35, 176, 0, menu.getScaledProgress(), 17)
        }
    }

    private fun renderEnergyMeter(guiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        guiGraphics.blit(texture, leftPos + 132, topPos + 28 + 47 - energyStored, 176, 64 - energyStored, 16, 47)
    }
}