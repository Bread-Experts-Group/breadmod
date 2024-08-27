package breadmod.util.gui.widget

import breadmod.util.render.rgMinecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

/**
 * A widget that renders an item slot.
 * @param pX The X position this slot will render at.
 * @param pY The Y position this slot will render at.
 * @param stack The [ItemStack] this widget renders.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SlotWidget(pX: Int, pY: Int, private val stack: ItemStack) : AbstractWidget(pX, pY, 16, 16, Component.empty()) {
    /**
     * Renders this [SlotWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if (!stack.isEmpty) {
            pGuiGraphics.renderItem(stack, x, y)
            pGuiGraphics.renderItemDecorations(rgMinecraft.font, stack, x, y)
        }
    }

    /**
     * Updates the narration output of this [SlotWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.HINT, stack.hoverName)
    }
}