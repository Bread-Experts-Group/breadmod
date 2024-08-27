package breadmod.client.screen

import breadmod.menu.block.SoundBlockMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

// todo buttons, packet for sending entered sound to block (Look at VoidTankPacket for reference), graphics
// todo second tab holding a library of all in-game sounds (possibly sorted into categories),
//  can preview each one and sort with a search bar (will have suggestions)
class SoundBlockScreen(
    pMenu: SoundBlockMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<SoundBlockMenu>(pMenu, pPlayerInventory, pTitle) {
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {

    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}