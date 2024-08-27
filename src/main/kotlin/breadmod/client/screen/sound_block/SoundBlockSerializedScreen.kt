package breadmod.client.screen.sound_block

import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.ContainerWidget
import breadmod.util.gui.widget.SlotWidget
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

    override fun tick() {
        rootWidget.tick()
    }

    override fun init() {
        super.init()
        rootWidget.x = (width / 2) - (rootWidget.width / 2)
        rootWidget.y = (height / 2) - (rootWidget.height / 2)
    }

    init {
        repeat(4) { rI ->
            repeat(9) { cI ->
                val linear = rI * 9 + cI
                rootWidget.addWidget(SlotWidget(cI * 16, rI * 16, pInventory.getItem(linear)), 0.0)
            }
        }
    }
}