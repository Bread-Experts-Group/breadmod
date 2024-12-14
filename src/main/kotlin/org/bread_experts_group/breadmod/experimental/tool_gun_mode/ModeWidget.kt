package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.util.render.rgMinecraft
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

abstract class ModeWidget(
    val title: Component,
    val icon: ItemStack
) : AbstractWidget(0, 0, 35, 40, title) {
    abstract val previewImage: ResourceLocation
    abstract val description: Component

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.pose().pushPose()
        guiGraphics.fill(
            RenderType.gui(),
            x,
            y,
            x + 35,
            y + 40,
            if (isHovered || isFocused) Color(16755200).rgb else Color.GRAY.rgb
        )
        guiGraphics.fill(RenderType.gui(), x + 1, y + 1, x + 34, y + 39, Color.DARK_GRAY.rgb)
        guiGraphics.drawScrollingString(
            rgMinecraft.font,
            message,
            x + 2,
            x + 33,
            y + 29,
            Color.WHITE.rgb
        )
        guiGraphics.pose().translate(x.toFloat() + 5.5f, y.toFloat() + 2, 0f)
        guiGraphics.pose().scaleFlat(1.5f)
        guiGraphics.renderFakeItem(icon, 0, 0)
        guiGraphics.pose().popPose()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

}