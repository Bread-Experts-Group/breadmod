package bread.mod.breadmod.registry

import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.command.client.AltToolGunModelCommand
import bread.mod.breadmod.item.toolGun.ToolGunAnimationHandler
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientCommandRegistrationEvent
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw

object ClientEvents {
    fun registerClientCommands() {
        ClientCommandRegistrationEvent.EVENT.register { dispatcher, sourceStack ->
            dispatcher.register(
                LiteralArgumentBuilder.literal<ClientCommandSourceStack>("breadmod")
                    .then(AltToolGunModelCommand.register())
            )
        }
    }

// --Commented out by Inspection START (9/10/2024 03:50):
//    fun registerKeyEvent() {
//        ClientRawInputEvent.KEY_PRESSED.register { client, keyCode, scanCode, action, modifiers ->
//            println("${client.player}, ${InputConstants.getKey(keyCode, scanCode)}")
//
//            return@register EventResult.pass()
//        }
//    }
// --Commented out by Inspection STOP (9/10/2024 03:50)

    fun registerMouseEvent() {
        ClientRawInputEvent.MOUSE_CLICKED_POST.register { client, button, action, mods ->
            // todo uhh.. tool gun functionality yes
            if (button == InputConstants.MOUSE_BUTTON_RIGHT && action == InputConstants.PRESS) {
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
    private fun registerOverlay(overlay: LayeredDraw.Layer) =
        ClientGuiEvent.RENDER_HUD.register(OverlayWrapper(overlay))

    private class OverlayWrapper(
        val overlay: LayeredDraw.Layer
    ) : ClientGuiEvent.RenderHud {
        override fun renderHud(
            graphics: GuiGraphics,
            deltaTracker: DeltaTracker
        ) = overlay.render(graphics, deltaTracker)
    }
}