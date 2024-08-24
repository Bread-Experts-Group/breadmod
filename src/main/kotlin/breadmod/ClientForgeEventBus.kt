package breadmod

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.client.gui.WarTicker
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.serverbound.tool_gun.ToolGunConfigurationPacket
import breadmod.util.gui.IHoldScreen
import breadmod.util.render.renderBuffer
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.Command
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.commands.Commands
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.commons.lang3.ArrayUtils

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventBus {
    @SubscribeEvent
    fun onLevelRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return
        renderBuffer.removeIf { (mutableList, renderStageEvent) -> renderStageEvent.invoke(mutableList, event) }
    }

    val changeMode = KeyMapping(
        "controls.${ModMain.ID}.$TOOL_GUN_DEF.change_mode",
        KeyConflictContext.GUI,
        KeyModifier.SHIFT,
        InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
        "controls.${ModMain.ID}.category.$TOOL_GUN_DEF"
    )

    val openGuiEditor = KeyMapping(
        "controls.${ModMain.ID}.gui_editor",
        KeyConflictContext.UNIVERSAL,
        KeyModifier.SHIFT,
        InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_F1),
        "controls.${ModMain.ID}.category"
    )

    var createdMappings = listOf<KeyMapping>()

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun logout(event: PlayerLoggedOutEvent) {
        rgMinecraft.options.keyMappings = ArrayUtils.removeElements(
            rgMinecraft.options.keyMappings,
            *createdMappings.toTypedArray()
        )
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun login(event: PlayerLoggedInEvent) {
        rgMinecraft.options.keyMappings = ArrayUtils.removeElements(
            rgMinecraft.options.keyMappings,
            openGuiEditor
        )
    }


    private fun modifierMatches(modifiers: Int, modifier: KeyModifier) = when (modifier) {
        KeyModifier.SHIFT -> modifiers and 0x0001
        KeyModifier.CONTROL -> modifiers and 0x0002
        KeyModifier.ALT -> modifiers and 0x0004
        KeyModifier.NONE -> 1
    } != 0

    private fun handleToolgunInput(
        player: LocalPlayer,
        itemHeld: ToolGunItem, stackHeld: ItemStack,
        key: InputConstants.Key, modifiers: Int,
        early: Boolean
    ): Boolean {
        val currentMode = itemHeld.getCurrentMode(stackHeld)

        if (!early && key == changeMode.key && modifierMatches(modifiers, changeMode.keyModifier)) {
            NETWORK.sendToServer(ToolGunConfigurationPacket(true))
            player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            return true
        } else {
            currentMode.keyBinds.forEach {
                toolGunBindList[it]?.let { bind ->
                    if (key == bind.key && modifierMatches(modifiers, bind.keyModifier)) {
                        NETWORK.sendToServer(ToolGunConfigurationPacket(false, it, early))

                        val mode = currentMode.mode
                        (if (early) mode::actionEarly else mode::action)(player.level(), player, stackHeld, it)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun <T> handleHoldScreenInput(
        holdScreen: T,
        key: InputConstants.Key,
        action: Int,
        modifiers: Int
    ) where T : Screen, T : IHoldScreen {
        if (
            action == InputConstants.RELEASE &&
            key == holdScreen.keyCheck.key &&
            modifierMatches(modifiers, holdScreen.keyCheck.keyModifier)
        ) holdScreen.onClose()
    }

    private fun handleInput(action: Int, key: InputConstants.Key, modifiers: Int, player: LocalPlayer?, screen: Screen?) {
        if (action == InputConstants.REPEAT) return
        if (screen is IHoldScreen) {
            handleHoldScreenInput(screen, key, action, modifiers)
        } else if (player != null && screen == null) {
            val stackHeld = player.mainHandItem
            val itemHeld = stackHeld.item

            if (itemHeld is ToolGunItem) handleToolgunInput(
                player,
                itemHeld, stackHeld,
                key, modifiers,
                action == InputConstants.PRESS
            )
        }
    }

    @SubscribeEvent
    fun keyInput(event: InputEvent.Key) {
        handleInput(
            event.action, InputConstants.getKey(event.key, event.scanCode), event.modifiers,
            rgMinecraft.player, rgMinecraft.screen
        )
    }

    @SubscribeEvent
    fun mouseInput(event: InputEvent.MouseButton.Post) {
        handleInput(
            event.action, InputConstants.Type.MOUSE.getOrCreate(event.button), event.modifiers,
            rgMinecraft.player, rgMinecraft.screen
        )
    }

    @SubscribeEvent
    fun registerClientCommands(event: RegisterClientCommandsEvent) {
        // todo it would probably be ideal to have each sub command be their own class for cleanness sakes
        // CommandMek.java
        event.dispatcher.register(Commands.literal("breadmod")
            .then(Commands.literal("increase_timer")
                .then(Commands.argument("amount", WarTimerArgument())
                    .executes { amount ->
                        try {
                            val arg = amount.getArgument("amount", Int::class.java)
                            WarTicker.increaseTimer(arg)
                        } catch (e: Exception) {
                            ModMain.LOGGER.error(e)
                        }
                        return@executes Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("reset_timer")
                .executes {
                    WarTicker.reset()
                    return@executes Command.SINGLE_SUCCESS
                }
            )
            .then(Commands.literal("start_timer")
                .executes {
                    WarTicker.active = true
                    return@executes Command.SINGLE_SUCCESS
                }
            )
            .then(Commands.literal("end_timer")
                .executes {
                    WarTicker.active = false
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
    }

    private class WarTimerArgument: ArgumentType<Int> {
        override fun parse(reader: StringReader): Int {
            return reader.readInt()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return

        WarTicker.tick()
    }
}