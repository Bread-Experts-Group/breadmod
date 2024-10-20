package breadmod.client.screen.tool_gun.creator

import breadmod.item.tool_gun.IToolGunMode.Companion.BASE_TOOL_GUN_DATA_PATH
import breadmod.item.tool_gun.ToolGunItem
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.SerializedScreenFactory
import breadmod.util.gui.widget.BackgroundWidget
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.gui.widget.TextWidget
import breadmod.util.render.rgMinecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import java.awt.Color

/**
 * Factory to create the spawn menu for the [ToolGunItem]'s [ToolGunCreatorMode].
 * @author Miko Elbrecht, Logan McLean (first iteration)
 * @since 1.0.0
 */
internal object ToolGunCreatorSpawnMenuFactory : SerializedScreenFactory<ToolGunCreatorMenu>(
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
        0, 0,
        398, 190,
        0f,
        Component.empty(),
        mutableMapOf(
            // left panel
            BackgroundWidget.SolidColorBackgroundWidget(
                0, 0,
                78, 190,
                Color(0f, 0f, 0f, 0.4f)
            ) to (0.0 to null),
            BackgroundWidget.SolidColorBackgroundWidget(
                1, 1,
                76, 93,
                Color(1f, 0f, 0f, 0.4f)
            ) to (0.1 to null),
            BackgroundWidget.SolidColorBackgroundWidget(
                1, 96,
                76, 93,
                Color(0f, 0f, 1f, 0.4f)
            ) to (0.1 to null),
            // middle panel
            BackgroundWidget.SolidColorBackgroundWidget(
                80, 0,
                212, 190,
                Color(0f, 0f, 0f, 0.4f)
            ) to (0.0 to null),
            BackgroundWidget.SolidColorBackgroundWidget(
                81, 1,
                210, 188,
                Color(0f, 0f, 0f, 0.4f)
            ) to (0.1 to null),
            // right panel
            BackgroundWidget.SolidColorBackgroundWidget(
                294, 0,
                104, 190,
                Color(0f, 0f, 0f, 0.4f)
            ) to (0.0 to null),
            BackgroundWidget.SolidColorBackgroundWidget(
                295, 1,
                102, 188,
                Color(0f, 0f, 0f, 0.4f)
            ) to (0.1 to null),
            TextWidget(
                3, 3,
                300, rgMinecraft.font.lineHeight,
                Component.literal("Test"),
                pCentering = TextWidget.CenteringType.NONE
            ) to (0.2 to null)
        )
    )

    override fun create(
        pMenu: ToolGunCreatorMenu,
        pInventory: Inventory,
        pTitle: Component
    ): ToolGunCreatorSerializedScreen =
        ToolGunCreatorSerializedScreen(
            pMenu,
            pInventory,
            pTitle,
            getRootWidget()
        )
}