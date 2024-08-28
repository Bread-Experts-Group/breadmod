package breadmod.client.screen.sound_block

import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.gui.widget.InventoryWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

internal class SoundBlockSerializedScreen(
    pMenu: SoundBlockMenu,
    pInventory: Inventory,
    pTitle: Component,
    rootWidget: ContainerWidget
) : SerializedScreen<SoundBlockMenu>(pMenu, pInventory, pTitle, rootWidget) {
    /**
     * Renders this [SoundBlockSerializedScreen].
     * @author Logan McLean (implementation), Miko Elbrecht (javadoc)
     * @since 1.0.0
     */
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun init() {
        super.init()
        val image = rootWidget.getTaggedWidget("background")
        rootWidget.x = (width - image.width) / 2
        rootWidget.y = (height - image.height) / 2

        rootWidget.addWidget(InventoryWidget(4, 35, pInventory), 0.0, "inventory")
    }
}