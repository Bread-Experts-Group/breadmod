package breadmod.util.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

/**
 * A widget that can contain other widgets.
 * @param pWidth The width of the widget.
 * @param pHeight The height of the widget.
 * @param pX The X position of the widget.
 * @param pY The Y position of the widget.
 * @param pComponent TODO something
 * @author Miko Elbrecht
 * @since 1.0
 */
class ContainerWidget(
    pWidth: Int, pHeight: Int,
    pX: Int, pY: Int,
    pComponent: Component
) : AbstractWidget(pWidth, pHeight, pX, pY, pComponent) {
    private val childrenWidgets = mutableListOf<Pair<AbstractWidget, Double>>()

    /**
     * Adds a widget to this container.
     * @return This container.
     * @param pWidget The widget to add.
     * @param pZIndex The Z index (rendering order) of the widget.
     * @author Miko Elbrecht
     * @since 1.0
     */
    fun addWidget(pWidget: AbstractWidget, pZIndex: Double): ContainerWidget = this.also {
        childrenWidgets.add(pWidget to pZIndex)
    }

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val pose = pGuiGraphics.pose()
        childrenWidgets.forEach { (widget, z) ->
            pose.pushPose()
            pose.translate(widget.x.toDouble(), widget.y.toDouble(), z)
            widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            pose.popPose()
        }
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, "w43")
    }

    override fun clicked(pMouseX: Double, pMouseY: Double): Boolean = false
}