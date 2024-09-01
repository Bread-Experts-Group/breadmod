package breadmod.client.screen.sound_block

import breadmod.menu.block.SoundBlockMenu
import breadmod.util.gui.SerializedScreen
import breadmod.util.gui.widget.*
import breadmod.util.gui.widget.ready.InventoryWidget
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import java.awt.Color
import java.util.*

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

        val title = TextWidget(
            0, 3,
            image.width, rgMinecraft.font.lineHeight,
            pMenu.parent.displayName, Color.DARK_GRAY,
            pNoScissor = true
        )

        rootWidget.addWidget(
            title
        ).addWidget(
            InventoryWidget(8, 88, pInventory, pMenu)
        ).addWidget(
            ItemContainerWidget(10, 10, 16, 16, pMenu.slots[36].container, pMenu).also {
                it.addWidget(SlotWidget(0, 0, it, 36))
            }
        ).addWidget(
            ListContainerWidget(
                image.x + 7, title.y + title.height, image.width - 14, 70, 6,
                {
                    mutableMapOf(
                        BackgroundWidget.SolidColorBackgroundWidget(
                            0, 0, it.width, it.height,
                            Color.DARK_GRAY
                        ) to (0.0 to null),
                        BackgroundWidget.SolidColorBackgroundWidget(
                            2, 2, it.width, it.height - 4,
                            Color.BLACK
                        ) to (1.0 to null)
                    )
                }, {
                    mutableMapOf(
                        BackgroundWidget.SolidColorBackgroundWidget(
                            0, 0, it.width, it.height,
                            Color.DARK_GRAY
                        ) to (2.0 to null),
                        BackgroundWidget.SolidColorBackgroundWidget(
                            1, 2, it.width - 3, it.height - 4,
                            Color.BLACK
                        ) to (3.0 to null),
                        ScrollBarWidget(it) to (0.0 to "scrollbar")
                    )
                }
            ), pTag = "list"
        )

        val list = rootWidget.getTaggedWidget("list") as ListContainerWidget
        val random = Random()

        repeat(300) {
            val newRow = RowContainerWidget(0, 0, rgMinecraft.font.lineHeight + 2)
            newRow.addWidget(
                BackgroundWidget.SolidColorBackgroundWidget(
                    0, 0,
                    list.width, newRow.height,
                    Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f)
                )
            )
            list.addWidget(newRow)
        }
    }
}