package breadmod.util.gui

import breadmod.ModMain.DATA_DIR
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.json
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import java.nio.file.Path

/**
 * A factory to create [Screen] instances from a serialized [ContainerWidget].
 * @param pPath The relative path to the JSON file.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SerializedScreenFactory<T : AbstractContainerMenu>(
    private val pPath: Path
) : MenuScreens.ScreenConstructor<T, SerializedScreen<T>> {
    private val screenFile = DATA_DIR.resolve(pPath).toFile()

    /**
     * Creates a default [ContainerWidget] for the factory. If the file at [pPath] does not exist,
     * this will be the saved widget.
     * @return The default [ContainerWidget].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract fun default(): ContainerWidget

    /**
     * @return The [ContainerWidget] from the JSON file at [pPath] or the default widget if the file doesn't exist.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getRootWidget(): ContainerWidget =
        if (screenFile.exists()) json.decodeFromString(screenFile.readText()) else default()

    /**
     * Creates an instance of [Screen] from the factory by loading a [ContainerWidget] from the JSON file at [pPath].
     * @return The created [Screen].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun create(pMenu: T, pInventory: Inventory, pTitle: Component): SerializedScreen<T> = SerializedScreen(
        pMenu,
        pInventory,
        pTitle,
        getRootWidget()
    )
}