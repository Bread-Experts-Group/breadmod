package breadmod.util.gui

/**
 * A screen that must be "held down" to remain open.
 * @property shouldClose Overrides the default behavior of the screen closing when the player releases the open key.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface IHoldScreen {
    var shouldClose: Boolean
}