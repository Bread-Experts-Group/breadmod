package breadmod.client.screen.sound_block

import breadmod.ModMain.modLocation
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
    private val texture = modLocation("textures", "gui", "container", "sound_block.png")
    private val imageWidth = 176
    private val imageHeight = 166

    /**
     * Renders this [SoundBlockSerializedScreen].
     * @author Logan McLean (implementation), Miko Elbrecht (javadoc)
     * @since 1.0.0
     */
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        pGuiGraphics.blit(texture, rootWidget.x, rootWidget.y, 0, 0, 176, 166)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun tick() {
        rootWidget.tick()
    }

    override fun init() {
        super.init()
        rootWidget.x = (width - imageWidth) / 2
        rootWidget.y = (height - imageHeight) / 2
    }

    init {
        for (rI in 3 downTo 0) repeat(9) { cI ->
            val linear = rI * 9 + cI
            rootWidget.addWidget(SlotWidget(cI * 16, rI * 16, pInventory.getItem(linear)), 0.0)
        }
    }
}