package breadmod.util.gui.widget

import breadmod.util.render.rgMinecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * A widget that supports a given [Container].
 * @param pX The X position this widget will render at.
 * @param pY The Y position this widget will render at.
 * @param pContainer The [Container] to attach to.
 * @param pContainerMenu The [AbstractContainerMenu] to attach to.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class ItemContainerWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val pContainer: Container,
    val pContainerMenu: AbstractContainerMenu
) : ContainerWidget(pX, pY, pWidth, pHeight, 0f, Component.empty(), mutableMapOf()) {
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        pContainerMenu.carried.let {
            if (!it.isEmpty) {
                val pose = pGuiGraphics.pose()
                pose.pushPose()
                pose.translate(0.0f, 0.0f, 232.0f)
                pGuiGraphics.renderItem(it, pMouseX, pMouseY)
                pGuiGraphics.renderItemDecorations(rgMinecraft.font, it, pMouseX, pMouseY)
                pose.popPose()
            }
        }
    }
}