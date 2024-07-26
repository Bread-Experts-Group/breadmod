package breadmod.block.machine.entity.screen

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.DoughMachineMenu
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.server.VoidTankPacket
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.formatUnit
import breadmod.util.renderFluid
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidType

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

        val showShort = !(minecraft ?: return).options.keyShift.isDown
        menu.parent.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.let {
            it.allTanks[0].let { tank ->
                val fluid = tank.fluid.fluid
                if(tank.fluidAmount > 0) {
                    val percentage = (tank.fluidAmount.toFloat() / tank.capacity) * 28
                    pGuiGraphics.renderFluid(
                        pX         = leftPos + 153F,
                        pY         = (topPos + 75F),
                        pWidth     = 16,
                        pHeight    = percentage.toInt(),
                        pFluid     = fluid,
                        pFlowing   = false,
                        pDirection = Direction.SOUTH
                    )
                }

                if(this.isHovering(153,46, 16, 28, pMouseX.toDouble(), pMouseY.toDouble())) {
                    pGuiGraphics.renderComponentTooltip(
                        this.font,
                        listOf(
                            modTranslatable(path = arrayOf("input")).withStyle { style ->
                                style
                                    .withColor(ChatFormatting.RED)
                                    .withItalic(true)
                            }
                                .append(" - ")
                                .append(Component.translatable(fluid.fluidType.descriptionId).withStyle { style ->
                                    style
                                        .withColor(IClientFluidTypeExtensions.of(fluid).tintColor)
                                        .withItalic(!fluid.fluidType.isAir)
                                }),
                            Component.literal(formatUnit(tank.fluidAmount, tank.capacity, "B", showShort, 2, -1, FluidType.BUCKET_VOLUME))
                        ),
                        pMouseX, pMouseY
                    )
                }
            }

            it.allTanks[1].let { tank ->
                val fluid = tank.fluid.fluid
                if(tank.fluidAmount > 0) {
                    val percentage = (tank.fluidAmount.toFloat() / tank.capacity) * 16
                    pGuiGraphics.renderFluid(
                        pX         = leftPos + 153F,
                        pY         = (topPos + 44F),
                        pWidth     = 16,
                        pHeight    = percentage.toInt(),
                        pFluid     = fluid,
                        pFlowing   = false,
                        pDirection = Direction.SOUTH
                    )
                }

                if(this.isHovering(153,28, 16, 16, pMouseX.toDouble(), pMouseY.toDouble())) {
                    pGuiGraphics.renderComponentTooltip(
                        this.font,
                        listOf(
                            modTranslatable(path = arrayOf("output")).withStyle { style ->
                                style
                                    .withColor(ChatFormatting.RED)
                                    .withItalic(true)
                            }
                                .append(" - ")
                                .append(Component.translatable(fluid.fluidType.descriptionId).withStyle { style ->
                                    style
                                        .withColor(IClientFluidTypeExtensions.of(fluid).tintColor)
                                        .withItalic(fluid.fluidType.isAir)
                                }),
                            Component.literal(formatUnit(tank.fluidAmount, tank.capacity, "B", showShort, 2, -1, FluidType.BUCKET_VOLUME))
                        ),
                        pMouseX, pMouseY
                    )
                }
            }
        }

        if(this.isHovering(132,28, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
            menu.parent.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.let {
                pGuiGraphics.renderComponentTooltip(
                    this.font,
                    listOf(
                        modTranslatable(path = arrayOf("energy"))
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.ITALIC),
                        Component.literal(formatUnit(it.energyStored, it.maxEnergyStored, "FE", showShort, 2))
                    ),
                    pMouseX, pMouseY
                )
            }
        }

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }

    override fun init() {
        super.init()
        addRenderableWidget(VoidTankButton(9, 121, 27, 1))
        addRenderableWidget(VoidTankButton(9, 121, 66, 0))
    }

    inner class VoidTankButton(pSize: Int, pX: Int, pY: Int, private val tankIndex: Int): AbstractButton(leftPos + pX, topPos + pY, pSize, pSize, Component.literal("x")) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            NETWORK.sendToServer(VoidTankPacket(menu.parent.blockPos, tankIndex))
        }
    }
}