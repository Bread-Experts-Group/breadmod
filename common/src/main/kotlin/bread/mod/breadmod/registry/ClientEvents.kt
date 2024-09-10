package bread.mod.breadmod.registry

import bread.mod.breadmod.client.gui.AbstractModGuiOverlay
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.command.client.AltToolGunModelCommand
import bread.mod.breadmod.item.tool_gun.ToolGunAnimationHandler
import bread.mod.breadmod.item.tool_gun.ToolGunItem
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientCommandRegistrationEvent
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

object ClientEvents {
    fun registerClientCommands() {
        ClientCommandRegistrationEvent.EVENT.register { dispatcher, sourceStack ->
            dispatcher.register(
                LiteralArgumentBuilder.literal<ClientCommandSourceStack>("breadmod")
                    .then(AltToolGunModelCommand.register())
            )
        }
    }

    fun registerKeyEvent() {
        ClientRawInputEvent.KEY_PRESSED.register { client, keyCode, scanCode, action, modifiers ->
            println("${client.player}, ${InputConstants.getKey(keyCode, scanCode)}")

            return@register EventResult.pass()
        }
    }

    fun registerMouseEvent() {
        ClientRawInputEvent.MOUSE_CLICKED_POST.register { client, button, action, mods ->
            val player = client.player ?: return@register null
            val item = player.useItem.item
            if (button == InputConstants.MOUSE_BUTTON_RIGHT && action == InputConstants.PRESS && item is ToolGunItem) {
                ToolGunAnimationHandler.trigger()
            }

            return@register EventResult.pass()
        }
    }

    fun registerOverlays() {
        registerOverlay(WarOverlay())
    }

    // todo some way to register overlays on a certain z-level?
    //  seems a bit unorthodox to translate by 3600 on the z axis to render above everything
    private fun registerOverlay(overlay: AbstractModGuiOverlay) =
        ClientGuiEvent.RENDER_HUD.register(OverlayWrapper(overlay))

    private class OverlayWrapper(
        val overlay: AbstractModGuiOverlay
    ) : ClientGuiEvent.RenderHud {
        override fun renderHud(
            graphics: GuiGraphics,
            deltaTracker: DeltaTracker
        ) {
            val gameWindow = rgMinecraft.window ?: return
            val screenWidth = gameWindow.guiScaledWidth
            val screenHeight = gameWindow.guiScaledHeight
            val poseStack = graphics.pose()
            val player = rgMinecraft.player ?: return
            val buffer = graphics.bufferSource()
            val partialTick = deltaTracker.realtimeDeltaTicks

            overlay.renderOverlay(graphics, partialTick, deltaTracker, screenWidth, screenHeight, poseStack, buffer, player)
        }
    }
}