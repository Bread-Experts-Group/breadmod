package breadmod.client.screen.sound_block

import breadmod.menu.block.SoundBlockMenu
import breadmod.network.serverbound.SoundBlockPacket
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.*
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
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

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val mouseKey = InputConstants.getKey(pKeyCode, pScanCode)
        return if (pKeyCode == InputConstants.KEY_ESCAPE ||
            rgMinecraft.options.keyInventory.isActiveAndMatches(mouseKey) && shouldCloseOnEsc()) {
            onClose()
            true
        } else super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun init() {
        super.init()
        val image = rootWidget.getTaggedWidget("background")
        rootWidget.x = (width - image.width) / 2
        rootWidget.y = (height - image.height) / 2

        rootWidget.addWidget(
            TextWidget(
                0, 3,
                image.width, rgMinecraft.font.lineHeight,
                pMenu.parent.displayName, Color.DARK_GRAY,
                pNoScissor = true
            ),
            200.0
        ).addWidget(
            InventoryWidget(8, 88, pInventory, pMenu),
            0.0
        ).addWidget(
            ItemContainerWidget(10, 10, 16, 16, pMenu.slots[36].container, pMenu).also {
                it.addWidget(SlotWidget(0, 0, it, 36), 0.0)
            }, 0.0
        ).addWidget(
            TestButton(0, 0, 20, 10) {
                println("clicked!")
                SoundBlockPacket(menu.parent.blockPos, SoundEvents.SHIELD_BLOCK.location.path)
            }, 0.0
        )
    }

    private class TestButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pOnPress: OnPress
    ): Button(pX, pY, pWidth, pHeight, Component.empty(), pOnPress, null)
}