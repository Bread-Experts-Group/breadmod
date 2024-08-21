package breadmod

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.serverbound.tool_gun.ToolGunConfigurationPacket
import breadmod.util.render.minecraft
import breadmod.util.render.renderBuffer
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
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
        KeyConflictContext.IN_GAME,
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
        minecraft.options.keyMappings = ArrayUtils.removeElements(
            minecraft.options.keyMappings,
            *createdMappings.toTypedArray()
        )
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun login(event: PlayerLoggedInEvent) {
        minecraft.options.keyMappings = ArrayUtils.removeElements(
            minecraft.options.keyMappings,
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
        key: InputConstants.Key, modifiers: Int
    ): Boolean {
        val currentMode = itemHeld.getCurrentMode(stackHeld)

        if (key == changeMode.key && modifierMatches(modifiers, changeMode.keyModifier)) {
            NETWORK.sendToServer(ToolGunConfigurationPacket(true))
            player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            return true
        } else {
            currentMode.keyBinds.forEach {
                toolGunBindList[it]?.let { bind ->
                    if (key == bind.key && modifierMatches(modifiers, bind.keyModifier)) {
                        NETWORK.sendToServer(ToolGunConfigurationPacket(false, it))
                        currentMode.mode.action(player.level(), player, stackHeld, it)
                        return true
                    }
                }
            }
        }
        return false
    }

    @SubscribeEvent
    fun keyInput(event: InputEvent.Key) {
        if (event.action != InputConstants.RELEASE) return
        val player = minecraft.player
        if (player == null) {

        } else if (minecraft.screen == null) {
            val stackHeld = player.mainHandItem
            val itemHeld = stackHeld.item

            if (itemHeld is ToolGunItem) handleToolgunInput(
                player,
                itemHeld, stackHeld,
                InputConstants.getKey(event.key, event.scanCode), event.modifiers
            )
        }
    }

    @SubscribeEvent
    fun mouseInput(event: InputEvent.MouseButton.Post) {
        if (event.action != InputConstants.RELEASE) return
        val player = minecraft.player
        if (player == null) {

        } else if (minecraft.screen == null) {
            val stackHeld = player.mainHandItem
            val itemHeld = stackHeld.item

            if (itemHeld is ToolGunItem) handleToolgunInput(
                player,
                itemHeld, stackHeld,
                InputConstants.Type.MOUSE.getOrCreate(event.button), event.modifiers
            )
        }
    }
}