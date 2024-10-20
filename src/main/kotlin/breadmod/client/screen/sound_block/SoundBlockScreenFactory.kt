package breadmod.client.screen.sound_block

import breadmod.ModMain.DATA_DIR
import breadmod.ModMain.modLocation
import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreenFactory
import breadmod.util.gui.widget.BackgroundWidget
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

internal object SoundBlockScreenFactory : SerializedScreenFactory<SoundBlockMenu>(
    DATA_DIR.resolve("sound_block_screen.json")
) {
    override fun default(): ContainerWidget = ContainerWidget(
        0, 0,
        176, 166,
        0f,
        Component.empty(),
        mutableMapOf(
            BackgroundWidget.TexturedBackgroundWidget(
                0, 0,
                176, 166,
                modLocation("textures", "gui", "container", "sound_block.png")
            ) to (0.0 to "background")
        ),
        true
    )

    override fun create(
        pMenu: SoundBlockMenu,
        pInventory: Inventory,
        pTitle: Component
    ): SoundBlockSerializedScreen =
        SoundBlockSerializedScreen(
            pMenu,
            pInventory,
            pTitle,
            getRootWidget()
        )
}