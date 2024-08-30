package breadmod.util.gui.widget

import breadmod.util.gui.widget.marker.IWidgetMouseClickSensitive
import breadmod.util.gui.widget.marker.IWidgetMouseMovementSensitive
import net.minecraft.network.chat.Component
import java.awt.Color

/**
 * A [ContainerWidget] that acts as a scrollbar. Required for use in [ListContainerWidget].
 * @param pParent The parent widget.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ScrollBarWidget(pParent: ContainerWidget) : ContainerWidget(
    0, 0,
    pParent.width, pParent.height, 0f,
    Component.empty(), mutableMapOf()
), IWidgetMouseClickSensitive, IWidgetMouseMovementSensitive {
    val background = BackgroundWidget.SolidColorBackgroundWidget(
        0, 0, width, height,
        Color.WHITE
    )

    override fun setHeight(pHeight: Int) {
        background.height = pHeight
        super.setHeight(pHeight)
    }

    init {
        addWidget(background, 100.0)
    }
}