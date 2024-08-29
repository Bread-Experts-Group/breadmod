package breadmod.util.gui.widget

import breadmod.util.gui.widget.marker.IWidgetKeySensitive
import breadmod.util.gui.widget.marker.IWidgetMouseClickSensitive
import breadmod.util.gui.widget.marker.IWidgetMouseDragSensitive
import breadmod.util.render.modifierMatches
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.inventory.ClickType
import net.minecraftforge.client.settings.KeyModifier
import java.awt.Color

/**
 * A widget that renders an item slot.
 * @param pX The X position this slot will render at.
 * @param pY The Y position this slot will render at.
 * @param pParent The [ItemContainerWidget] this slot attaches to.
 * @param slot The slot index in the [ItemContainerWidget]'s [Container].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SlotWidget(
    pX: Int, pY: Int,
    val pParent: ItemContainerWidget,
    val slot: Int
) : AbstractWidget(pX, pY, 16, 16, Component.empty()), IWidgetMouseClickSensitive, IWidgetMouseDragSensitive,
    IWidgetKeySensitive {
    private val hoverColor = Color(1f, 1f, 1f, 0.5f).rgb

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val stack = pParent.pContainer.getItem(slot)
        val notEmpty = !stack.isEmpty

        if (notEmpty) {
            pGuiGraphics.renderItem(stack, 0, 0)
            pGuiGraphics.renderItemDecorations(rgMinecraft.font, stack, 0, 0)
        }

        val isHoveredLocal = isMouseOver(pMouseX.toDouble(), pMouseY.toDouble())
        if (isHoveredLocal) {
            pGuiGraphics.fill(RenderType.guiOverlay(), 0, 0, width, height, hoverColor)
            if (pParent.pContainerMenu.carried.isEmpty && notEmpty)
                pGuiGraphics.renderTooltip(rgMinecraft.font, stack, pMouseX - x, pMouseY - y)
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        rgMinecraft.gameMode?.let {
            val player = rgMinecraft.player ?: return false
            it.handleInventoryMouseClick(pParent.pContainerMenu.containerId, slot, pButton, ClickType.PICKUP, player)
        }
        return true
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        return if (!pParent.pContainerMenu.carried.isEmpty) {
            rgMinecraft.gameMode?.let {
                // TODO drag logic
            }
            true
        } else false
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val stack = pParent.pContainer.getItem(slot)
        return if (!stack.isEmpty) {
            val key = InputConstants.getKey(pKeyCode, pScanCode)
            if (rgMinecraft.options.keyDrop.isActiveAndMatches(key)) {
                rgMinecraft.gameMode?.let {
                    val player = rgMinecraft.player ?: return false
                    it.handleInventoryMouseClick(
                        pParent.pContainerMenu.containerId, slot,
                        if (modifierMatches(pModifiers, KeyModifier.CONTROL)) 1 else 0,
                        ClickType.THROW, player
                    )
                }
                true
            } else false
        } else false
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        val stack = pParent.pContainer.getItem(slot)
        pNarrationElementOutput.add(NarratedElementType.HINT, stack.hoverName)
    }

    init {
        pParent.addWidget(this, 0.0, "slot$slot")
    }
}