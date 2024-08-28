package breadmod.client.screen.sound_block

import breadmod.ModMain.DATA_DIR
import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreenFactory
import breadmod.util.gui.widget.ContainerWidget
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

internal object SoundBlockScreenFactory : SerializedScreenFactory<SoundBlockMenu>(
    DATA_DIR.resolve("sound_block_screen.json")
) {
    override fun default(): ContainerWidget = ContainerWidget(
        600, 600,
        0, 0,
        0f,
        Component.empty(),
        mutableMapOf()
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