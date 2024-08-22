package breadmod.client.screen.tool_gun.creator

import breadmod.item.tool_gun.IToolGunMode.Companion.BASE_TOOL_GUN_DATA_PATH
import breadmod.item.tool_gun.ToolGunItem
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.gui.SerializedScreenFactory
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory

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
        mutableMapOf()
    )

    override fun create(pMenu: ToolGunCreatorMenu, pInventory: Inventory, pTitle: Component): ToolGunCreatorSerializedScreen =
        ToolGunCreatorSerializedScreen(
            pMenu,
            pInventory,
            pTitle,
            getRootWidget()
        )
}