package breadmod.util.gui.widget.marker

/**
 * An interface for widgets that are aware of their offset.
 * @property absoluteX The absolute X position of this widget.
 * @property absoluteY The absolute Y position of this widget.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface IWidgetOffsetAware {
    var absoluteX: Int
    var absoluteY: Int
}