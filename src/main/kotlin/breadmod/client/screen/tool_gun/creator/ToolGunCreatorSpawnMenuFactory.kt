package breadmod.client.screen.tool_gun.creator

import breadmod.item.tool_gun.IToolGunMode.Companion.BASE_TOOL_GUN_DATA_PATH
import breadmod.item.tool_gun.ToolGunItem
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.SerializedScreenFactory
import breadmod.util.gui.widget.BackgroundWidget
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.gui.widget.TextWidget
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import java.awt.Color

/**
 * Factory to create the spawn menu for the [ToolGunItem]'s [ToolGunCreatorMode].
 * @author Miko Elbrecht, Logan McLean (first iteration)
 * @since 1.0.0
 */
object ToolGunCreatorSpawnMenuFactory : SerializedScreenFactory<ToolGunCreatorMenu>(
    BASE_TOOL_GUN_DATA_PATH.resolve("creator/screens/spawn_menu.json")
) {
    internal val ENTITY_PATH = BASE_TOOL_GUN_DATA_PATH.resolve("creator/entity.json").toFile()

    internal val loadedEntity: ToolGunCreatorMode.CreatorEntity = ToolGunCreatorMode.CreatorEntity(
        null,
        EntityType.PIG,
        20.0,
        0.25,
        mutableMapOf(),
        40.0
    )
    internal var lastSaved: String? = null

    override fun default(): ContainerWidget = ContainerWidget(
        200, 100,
        0, 0,
        Component.literal("Test"),
        mutableMapOf(
            BackgroundWidget.SolidColorBackgroundWidget(
                0, 0,
                200, 32,
                Color(0f, 0f, 0f, 0.5f)
            ) to -0.1,
            TextWidget(
                0, 0,
                200, 32,
                Component.literal("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest"),
                Color(0f, 0f, 0f, 1f), Color(0f, 0f, 0f, 0f),
                TextWidget.CenteringType.CENTER_LINE, TextWidget.WrappingType.WRAP_VERTICAL
            ) to 0.0
        )
    )

    override fun create(pMenu: ToolGunCreatorMenu, pInventory: Inventory, pTitle: Component): ToolGunCreatorSerializedScreen =
        ToolGunCreatorSerializedScreen(
            pMenu,
            pInventory,
            pTitle,
            getRootWidget()
        )
}