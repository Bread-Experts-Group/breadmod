package breadmod.item.screen

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.DoughMachineMenu
import breadmod.item.menu.CertificateMenu
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.VoidTankPacket
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.formatUnit
import breadmod.util.renderFluid
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidType

class CertificateScreen(
    pMenu: CertificateMenu,
    pPlayerInventory: Inventory,
    pTitle: Component,
) : AbstractContainerScreen<CertificateMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        val TEXTURE = modLocation("textures", "gui", "item", "certificate.png")
    }

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F)
        RenderSystem.setShaderTexture(0, TEXTURE)

        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}