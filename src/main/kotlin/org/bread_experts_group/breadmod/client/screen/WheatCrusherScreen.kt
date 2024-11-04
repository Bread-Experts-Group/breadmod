package org.bread_experts_group.breadmod.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.menu.WheatCrusherMenu

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
    }
}