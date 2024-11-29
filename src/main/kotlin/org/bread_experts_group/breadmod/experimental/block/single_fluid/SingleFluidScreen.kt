package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import java.awt.Color

class SingleFluidScreen(
    menu: SingleFluidRecipeMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<SingleFluidRecipeMenu>(menu, inventory, title) {
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.fill(RenderType.gui(), leftPos, topPos, leftPos + 173, topPos + 200, Color.RED.rgb)
        guiGraphics.fill(
            RenderType.gui(),
            leftPos + 1,
            topPos + 1,
            leftPos + 172,
            topPos + 199,
            Color(150, 150, 150).rgb
        )

        guiGraphics.drawString(
            rgMinecraft.font,
            "progress: ${menu.parent.progress}",
            leftPos + 10,
            topPos + 20,
            Color.WHITE.rgb,
        )
        guiGraphics.drawString(
            rgMinecraft.font,
            "max progress: ${menu.parent.maxProgress}",
            leftPos + 80,
            topPos + 20,
            Color.WHITE.rgb
        )
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}