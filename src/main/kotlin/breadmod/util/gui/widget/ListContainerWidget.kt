package breadmod.util.gui.widget

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

/**
 * A widget that renders a list of [RowContainerWidget]s.
 * @param pX The X position this widget will render at.
 * @param pY The Y position this widget will render at.
 * @param pWidth The width of this widget.
 * @param pHeight The height of this widget.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ListContainerWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pScrollBarWidth: Int,
    pBackgroundComponents: MutableMap<AbstractWidget, Pair<Double, String?>>
) : ContainerWidget(pX, pY, pWidth, pHeight, 0f, Component.empty(), pBackgroundComponents) {
    override fun addWidget(pWidget: AbstractWidget, pZIndex: Double, pTag: String?): ContainerWidget {
        if (pWidget !is RowContainerWidget)
            throw UnsupportedOperationException("ListContainerWidget only supports RowContainerWidgets")
        pWidget.width = width
        return super.addWidget(pWidget, pZIndex, pTag)
    }
}