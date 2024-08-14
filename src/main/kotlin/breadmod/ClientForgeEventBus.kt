package breadmod

import breadmod.ClientModEventBus.createMappingsForControls
import breadmod.ClientModEventBus.toolGunBindList
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.tool_gun.ToolGunPacket
import breadmod.util.render.renderBuffer
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.commons.lang3.ArrayUtils

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventBus {
    /**
     * Level scene render event.
     * @see renderBuffer
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @SubscribeEvent
    fun onLevelRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return
        renderBuffer.removeIf { (mutableList, renderStageEvent) -> renderStageEvent.invoke(mutableList, event) }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun myLogin(event: PlayerLoggedInEvent) {
        val options = Minecraft.getInstance().options
        options.keyMappings = ArrayUtils.removeElements(options.keyMappings, *createMappingsForControls().toTypedArray())
    }

//    val keyMap = KeyMapping("tool gun test", GLFW.GLFW_KEY_H, "breadmod")
    val changeMode = KeyMapping(
        "controls.${ModMain.ID}.$TOOL_GUN_DEF.change_mode",
        KeyConflictContext.IN_GAME,
        KeyModifier.SHIFT,
        InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
        "controls.${ModMain.ID}.category.$TOOL_GUN_DEF"
    )

    @SubscribeEvent
    fun keyInput(event: InputEvent.Key) {
        val instance = Minecraft.getInstance()
        val player: LocalPlayer = instance.player ?: return
        val level = instance.level ?: return
        val stack = player.mainHandItem
        val item = stack.item

        if(item is ToolGunItem) {
            val currentMode = item.getCurrentMode(stack)
            if(player.isHolding(item) && changeMode.consumeClick() &&
                event.action == InputConstants.PRESS) {
                NETWORK.sendToServer(ToolGunPacket(true))
                player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            } else {
                currentMode.keyBinds.forEach {
                    val bind = toolGunBindList[it] ?: return@forEach
                    if(player.isHolding(item) && event.key == bind.key.value && event.action == InputConstants.PRESS) {
                        NETWORK.sendToServer(ToolGunPacket(false, it))
                        println("firing action")
                        currentMode.mode.action(level, player, stack, it)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun mouseInput(event: InputEvent.MouseButton.Post) {
        val instance = Minecraft.getInstance()
        val player: LocalPlayer = instance.player ?: return
        val level = instance.level ?: return
        val stack = player.mainHandItem
        val item = stack.item

        if(item is ToolGunItem) {
//            println(event.modifiers)
            val currentMode = item.getCurrentMode(stack)
            currentMode.keyBinds.forEach {
                val bind = toolGunBindList[it] ?: return@forEach
                if(player.isHolding(item) && event.button == bind.key.value
                    && event.action == InputConstants.PRESS) {
                    NETWORK.sendToServer(ToolGunPacket(false, it))
                    println("firing action")
                    currentMode.mode.action(level, player, stack, it)
                }
            }
        }
    }

    @SubscribeEvent
    fun playerTick(event: TickEvent.ClientTickEvent) {
        val instance = Minecraft.getInstance()
        val player = instance.player ?: return
        val level = instance.level ?: return
        val stack = player.mainHandItem
        val item = stack.item

        if(item is ToolGunItem) {
            val currentMode = item.getCurrentMode(stack)
            if(player.isHolding(item)) {
                currentMode.mode.open(level, player, stack, null)
            } else {
                currentMode.mode.close(level, player, stack, null)
            }
        }
    }

//    @SubscribeEvent
//    fun clientTick(event: TickEvent.ClientTickEvent) {
//        if(event.phase != TickEvent.Phase.END) return
//
//        val instance = Minecraft.getInstance()
//        val level = instance.level ?: return
//        val player = instance.player ?: return
//        val stack = player.mainHandItem
//        val item = stack.item
//
//        if(item is ToolGunItem && player.isHolding(stack.item)) {
//            val currentMode = item.getCurrentMode(stack)
//            if(level.isClientSide) {
//                if(!player.isHolding(item)) currentMode.mode.close(level, player, stack, null)
//                else {
//                    if(ToolGunItem.changeMode.consumeClick()) {
//                        NETWORK.sendToServer(ToolGunPacket(true))
//                        player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
//                    } else {
//                        currentMode.mode.open(level, player, stack, null)
//                        currentMode.keyBinds.forEach {
//                            val bind = toolGunBindList[it]
//                            if(bind != null && bind.consumeClick()) {
//                                NETWORK.sendToServer(ToolGunPacket(false, it))
//                                currentMode.mode.action(level, player, stack, it)
//                            }
//                        }
//                    }
//                }
//            } else {
//                if(player.isHolding(item)) currentMode.mode.open(level, player, stack, null)
//                else currentMode.mode.close(level, player, stack, null)
//            }
//        }
//    }

//    @Suppress("UNUSED_PARAMETER")
//    @SubscribeEvent
//    fun onTick(event: TickEvent.ClientTickEvent) {
//        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return
//
//        ToolGunAnimationHandler.tick()
//    }
}