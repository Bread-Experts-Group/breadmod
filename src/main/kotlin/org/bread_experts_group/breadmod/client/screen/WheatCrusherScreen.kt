package org.bread_experts_group.breadmod.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.menu.WheatCrusherMenu
import org.bread_experts_group.breadmod.util.formatUnit

class WheatCrusherScreen(
    menu: WheatCrusherMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<WheatCrusherMenu>(menu, inventory, title) {
    private val texture = modLocation("textures", "gui", "container", "wheat_crusher.png")

    init {
        imageWidth = 176
        imageHeight = 198
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, texture)

        guiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        inventoryLabelY = imageHeight - 94

        renderProgressArrow(guiGraphics)
        renderEnergyMeter(guiGraphics)
    }

    private var step: Int = -32
    private var timer: Int = 20
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val showShort = !(minecraft ?: return).options.keyShift.isDown
        if (this.isHovering(151, 14, 16, 47, mouseX.toDouble(), mouseY.toDouble())) {
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

        // todo should be updated using [rgMinecraft.gui.guiTicks] for a consistent 20 ticks per second baseline
        if (menu.isCrafting()) {
            // Left crushing wheel
            guiGraphics.blit(texture, leftPos + 51, topPos + 38, 176, step, 32, 32)
            // Right crushing wheel
            guiGraphics.blit(texture, leftPos + 92, topPos + 38, 208, step, 32, 32)
            if (timer <= 0) {
                timer = 40
                if (step < 32) step += 32 else step = -32
            } else timer -= 2
        } else step = -32

//        println(menu.parent.progress)
//        println(menu.parent.maxProgress)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    private fun renderEnergyMeter(guiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        guiGraphics.blit(texture, leftPos + 151, topPos + 14 + 47 - energyStored, 176, 111 - energyStored, 16, 47)
    }

    private fun renderProgressArrow(guiGraphics: GuiGraphics) {
        if (menu.isCrafting()) {
            guiGraphics.blit(texture, leftPos + 83, topPos + 32, 192, 64, 9, menu.getScaledProgress())
        }
    }
}