package breadmod.util.gui.widget

import breadmod.util.gui.widget.marker.IWidgetMouseClickSensitive
import breadmod.util.gui.widget.marker.IWidgetMouseDragSensitive
import net.minecraft.network.chat.Component
import org.joml.Math.clamp
import java.awt.Color
import kotlin.math.roundToInt

/**
 * A [ContainerWidget] that acts as a scrollbar. Required for use in [ListContainerWidget].
 * @param pParent The parent widget.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ScrollBarWidget(private val pParent: ContainerWidget) : ContainerWidget(
    0, 0,
    pParent.width, pParent.height, 0f,
    Component.empty(), mutableMapOf()
), IWidgetMouseClickSensitive, IWidgetMouseDragSensitive {
    // TODO: Extensibility
    private val background = BackgroundWidget.SolidColorBackgroundWidget(
        0, 0, width, height,
        Color.WHITE
    )

    private var respondingToDrag = false
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        respondingToDrag = true
        return true
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return if (respondingToDrag) {
            respondingToDrag = false
            true
        } else super.mouseReleased(pMouseX, pMouseY, pButton)
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        return if (respondingToDrag) {
            y = clamp(0, pParent.height - height, y + pDragY.roundToInt())
            true
        } else super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
    }

    /**
     * Sets the height of this [ScrollBarWidget], aswell as resizing its visible background.
     * @param pHeight The new height of this [ScrollBarWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun setHeight(pHeight: Int) {
        val restrictedHeight = clamp(4, pParent.height, pHeight)
        background.height = restrictedHeight
        super.setHeight(restrictedHeight)
    }

    init {
        addWidget(background, 100.0)
    }
}