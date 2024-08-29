package breadmod.client.screen.sound_block

import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.gui.widget.InventoryWidget
import breadmod.util.gui.widget.TextWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import java.awt.Color

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

        rootWidget.addWidget(
            TextWidget(2, 2, 20, 10, Component.literal("A TEST"), Color(0f, 0f, 0f, 0f)),
            0.3, "title"
        )
        rootWidget.addWidget(InventoryWidget(8, 88, pInventory, pMenu), 0.0, "inventory")
    }
}