package breadmod.util.gui.widget

import breadmod.util.gui.widget.marker.IWidgetMouseScrollSensitive
import breadmod.util.gui.widget.marker.IWidgetOffsetAware
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.max

/**
 * A widget that renders a list of [RowContainerWidget]s.
 * @param pX The X position this widget will render at.
 * @param pY The Y position this widget will render at.
 * @param pWidth The width of this widget.
 * @param pHeight The height of this widget.
 * @param pScrollBarWidth The width of the scrollbar.
 * @param pBackgroundWidgets The widgets for the background of the contents.
 * @param pScrollWidgets The widgets for the scrollbar. (One widget should be tagged "scrollbar" and be a ScrollBarWidget)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ListContainerWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,

    pScrollBarWidth: Int,
    pBackgroundWidgets: (ContainerWidget.(backgroundContainer: ContainerWidget) -> ContainedWidgets),
    pScrollWidgets: (ContainerWidget.(scrollContainer: ContainerWidget) -> ContainedWidgets)
) : ContainerWidget(pX, pY, pWidth, pHeight, 0f, Component.empty(), mutableMapOf()), IWidgetOffsetAware {
    /**
     * Container for the contents of this [ListContainerWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val scrollContentsContainer: ContainerWidget = object : ContainerWidget(
        0, 0,
        pWidth - pScrollBarWidth, 0, 0f,
        Component.empty(), mutableMapOf()
    ), IWidgetMouseScrollSensitive {}

    /**
     * Container for the background of the contents of this [ListContainerWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val scrollContentsBackgroundContainer: ContainerWidget = ContainerWidget(
        scrollContentsContainer.x, scrollContentsContainer.y,
        scrollContentsContainer.width, height, 0f,
        Component.empty(), mutableMapOf()
    )

    /**
     * Container for the scrollbar of this [ListContainerWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val scrollBarContainer: ContainerWidget = ContainerWidget(
        width - pScrollBarWidth, 0,
        pScrollBarWidth, pHeight, 0f,
        Component.empty(), mutableMapOf()
    )
    private val scrollBar: ScrollBarWidget

    override fun addWidget(pWidget: AbstractWidget, pZIndex: Double, pTag: String?): ContainerWidget {
        if (pWidget !is RowContainerWidget)
            throw UnsupportedOperationException("ListContainerWidget only supports RowContainerWidgets")
        pWidget.width = width
        scrollContentsContainer.height += pWidget.height
        pWidget.y = scrollContentsContainer.height
        scrollBar.height = scrollBarContainer.height *
                (scrollBarContainer.height / max(scrollContentsContainer.height, scrollBarContainer.height))
        return this
    }

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val pose = pGuiGraphics.pose()

        fun positioning(widget: ContainerWidget) {
            pose.pushPose()
            pose.translate(widget.x.toDouble(), widget.y.toDouble(), 0.0)
            widget.render(
                pGuiGraphics,
                pMouseX, pMouseX,
                pPartialTick
            )
            pGuiGraphics.fill(0, 0, widget.width, widget.height, Color(0.5f, 0f, 0f, 0.5f).rgb)
            pose.popPose()
        }

        positioning(scrollContentsBackgroundContainer)
        positioning(scrollContentsContainer)
        positioning(scrollBarContainer)
    }

    init {
        pScrollWidgets.invoke(this, scrollBarContainer).forEach { (widget, pair) ->
            scrollBarContainer.addWidget(widget, pair.first, pair.second)
        }
        scrollBar = scrollBarContainer.getTaggedWidget("scrollbar") as ScrollBarWidget
        pBackgroundWidgets.invoke(this, scrollContentsBackgroundContainer).forEach { (widget, pair) ->
            scrollContentsBackgroundContainer.addWidget(widget, pair.first, pair.second)
        }

        @Suppress("RedundantValueArgument")
        super.addWidget(scrollContentsBackgroundContainer, 0.0, "background")
        @Suppress("RedundantValueArgument")
        super.addWidget(scrollContentsContainer, 0.0, "contents")
        @Suppress("RedundantValueArgument")
        super.addWidget(scrollBarContainer, 0.0, "scroll")
    }

    override var absoluteX: Int = 0
    override var absoluteY: Int = 0
}