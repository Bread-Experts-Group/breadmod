package breadmod.util.gui.widget

import net.minecraft.network.chat.Component

/**
 * A [ContainerWidget] designed for use in [ListContainerWidget]s.
 * The width of this widget is set to 0, as it is expected to be set by the parent [ListContainerWidget].
 * @param pX The X position this widget will render at.
 * @param pY The Y position this widget will render at.
 * @param pHeight The height of this widget.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class RowContainerWidget(
    pX: Int, pY: Int, pHeight: Int
) : ContainerWidget(pX, pY, 0, pHeight, 0f, Component.empty(), mutableMapOf())