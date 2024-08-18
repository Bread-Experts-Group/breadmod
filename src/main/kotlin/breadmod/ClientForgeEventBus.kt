package breadmod

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.tool_gun.ToolGunPacket
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
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventBus {
    @SubscribeEvent
    fun onLevelRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return
        renderBuffer.removeIf { (mutableList, renderStageEvent) -> renderStageEvent.invoke(mutableList, event) }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun myLogin(event: PlayerLoggedInEvent) {
        //val options = minecraft.options
        //options.keyMappings = ArrayUtils.removeElements(options.keyMappings, *createMappingsForControls().toTypedArray())
    }

    val changeMode = KeyMapping(
        "controls.${ModMain.ID}.$TOOL_GUN_DEF.change_mode",
        KeyConflictContext.IN_GAME,
        KeyModifier.SHIFT,
        InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
        "controls.${ModMain.ID}.category.$TOOL_GUN_DEF"
    )

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
            NETWORK.sendToServer(ToolGunPacket(true))
            player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            return true
        } else {
            currentMode.keyBinds.forEach {
                toolGunBindList[it]?.let { bind ->
                    if (key == bind.key && modifierMatches(modifiers, bind.keyModifier)) {
                        NETWORK.sendToServer(ToolGunPacket(false, it))
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
        if (event.action != InputConstants.RELEASE || minecraft.screen != null) return

        val player = minecraft.player ?: return
        val stackHeld = player.mainHandItem
        val itemHeld = stackHeld.item

        if (itemHeld is ToolGunItem) handleToolgunInput(
            player,
            itemHeld, stackHeld,
            InputConstants.getKey(event.key, event.scanCode), event.modifiers
        )
    }

    @SubscribeEvent
    fun mouseInput(event: InputEvent.MouseButton.Post) {
        if (event.action != InputConstants.RELEASE || minecraft.screen != null) return

        val player = minecraft.player ?: return
        val stackHeld = player.mainHandItem
        val itemHeld = stackHeld.item

        if (itemHeld is ToolGunItem) handleToolgunInput(
            player,
            itemHeld, stackHeld,
            InputConstants.Type.MOUSE.getOrCreate(event.button), event.modifiers
        )
    }
}