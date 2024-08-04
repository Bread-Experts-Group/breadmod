package breadmod.client.screen

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.menu.block.WheatCrusherMenu
import breadmod.util.capability.EnergyBattery
import breadmod.util.formatUnit
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.common.capabilities.ForgeCapabilities

class WheatCrusherScreen(
    pMenu: WheatCrusherMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<WheatCrusherMenu>(pMenu, pPlayerInventory, pTitle) {
    private val texture = modLocation("textures", "gui", "container", "wheat_crusher.png")
    private val textureWidth = 176
    private val textureHeight = 198

    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        RenderSystem.setShaderTexture(0, texture)

        pGuiGraphics.blit(texture, leftPos, topPos, 0, 0, textureWidth, textureHeight)
        inventoryLabelY = textureHeight - 94

        renderProgressArrow(pGuiGraphics)
        renderEnergyMeter(pGuiGraphics)
    }

    private var step: Int = -32; private var timer: Int = 20
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, delta: Float) {
        renderBackground(pGuiGraphics)
        super.render(pGuiGraphics, pMouseX, pMouseY, delta)

        val showShort = !minecraft!!.options.keyShift.isDown
        if(this.isHovering(151,14, 16, 47, pMouseX.toDouble(), pMouseY.toDouble())) {
            menu.parent.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.let {
                pGuiGraphics.renderComponentTooltip(
                    this.font,
                    listOf(
                        modTranslatable(path = arrayOf("energy"))
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.ITALIC),
                        Component.literal(formatUnit(it.energyStored, it.maxEnergyStored, "FE", showShort, 2))
                    ),
                    pMouseX, pMouseY
                )
            }
        }

        if(menu.isCrafting()) { // The timer might be irrelevant in this since the code runs every tick, but I'm not sure yet
            // Left crushing wheel
            pGuiGraphics.blit(texture, leftPos + 51, topPos + 38, 176, step, 32, 32)
            // Right crushing wheel
            pGuiGraphics.blit(texture, leftPos + 92, topPos + 38, 208, step, 32, 32)
            if(timer <= 0) {
                timer = 40
                if(step < 32) step += 32 else step = -32
            } else timer -=2
        } else step = -32

        renderTooltip(pGuiGraphics, pMouseX, pMouseY)

    }

    private fun renderEnergyMeter(pGuiGraphics: GuiGraphics) {
        val energyStored = menu.getEnergyStoredScaled()
        pGuiGraphics.blit(texture, leftPos + 151, topPos + 14 + 47 - energyStored, 176, 111 - energyStored, 16, 47)
    }

    private fun renderProgressArrow(pGuiGraphics: GuiGraphics) {
        if(menu.isCrafting()) {
            pGuiGraphics.blit(texture, leftPos + 83, topPos + 32, 192, 64, 9, menu.getScaledProgress())
        }
    }
}