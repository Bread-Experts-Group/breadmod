package org.bread_experts_group.breadmod

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.KeyMapping
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth.clamp
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import org.bread_experts_group.breadmod.client.gui.WarOverlay
import org.bread_experts_group.breadmod.experimental.tool_gun_mode.TestScreen
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.PhysXTestTool
import org.bread_experts_group.breadmod.util.render.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

// todo register the other client stuff later
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
internal object ClientNeoForgeEventBus {
    @SubscribeEvent
    fun registerStageRender(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_SKY && WarOverlay.timerActive) {
            val poseStack = event.poseStack
            val bufferBuilder =
                Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
            val millis = Util.getMillis()

            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            poseStack.pushPose()
            poseStack.mulPose(Axis.XP.rotationDegrees(-17f))
            val matrix = poseStack.last().pose()
            bufferBuilder.addVertex(matrix, 0f, 100f, 0f).setColor(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f))

            for (j: Int in 0..16) {
                val f1 = j * (Math.PI.toFloat() * 2f) / 16f
                val f2: Float = sin(f1)
                val f3: Float = cos(f1)
                bufferBuilder.addVertex(matrix, f2, -1f, -f3).setColor(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f))
            }

            val shaderFogColor = RenderSystem.getShaderFogColor()
            RenderSystem.setShaderFogColor(
                shaderFogColor[0] + redness,
                shaderFogColor[1] - redness,
                shaderFogColor[2] - redness,
                1f
            )
            FogRenderer.setupFog(
                event.camera,
                FogRenderer.FogMode.FOG_SKY,
                256f,
                true,
                event.partialTick.realtimeDeltaTicks
            )
            FogRenderer.setupFog(
                event.camera,
                FogRenderer.FogMode.FOG_TERRAIN,
                max(256f, 32f),
                true,
                event.partialTick.realtimeDeltaTicks
            )

            redness = clamp((sin(millis.toFloat() / 1800) + 1) / 2, 0f, 1f)
            skyColorMixinActive = true

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
            RenderSystem.disableBlend()
            poseStack.popPose()
        } else if (!WarOverlay.timerActive) {
            redness = 0.0f
            skyColorMixinActive = false
        }

        if (event.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            renderBuffer.removeIf { (mutableList, renderEvent) ->
                renderEvent.invoke(mutableList, event)
            }
        }
    }

    @SubscribeEvent
    fun onMouseEvent(event: MouseScrollingEvent) {
        val player = rgMinecraft.player ?: return
        val stack = player.getItemInHand(player.usedItemHand)
        if (player.isShiftKeyDown and stack.`is`(ModItems.TOOL_GUN)) {
            player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 1f)
            player.sendSystemMessage(Component.literal("Scrolling cancelled!"))
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onKeyboardPress(event: InputEvent.Key) {
        val player = rgMinecraft.player ?: return
        val stack = player.getItemInHand(player.usedItemHand)
        if (event.key == openModeGui.key.value && stack.`is`(ModItems.TOOL_GUN) && rgMinecraft.screen == null) {
            rgMinecraft.setScreen(TestScreen(Component.literal("Tool Gun: Mode Select")))
        }
    }

    val openModeGui = KeyMapping(
        "controls.${BreadMod.ID}.mode_screen",
        KeyConflictContext.UNIVERSAL,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_R),
        "controls.${BreadMod.ID}"
    )

//    val openGuiEditor = KeyMapping(
//        "controls.${BreadMod.ID}.gui_editor",
//        KeyConflictContext.UNIVERSAL,
//        KeyModifier.SHIFT,
//        InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_F1),
//        "controls.${BreadMod.ID}.category"
//    )

//    private fun <T> handleHoldScreenInput(
//        holdScreen: T,
//        key: InputConstants.Key,
//        action: Int,
//        modifiers: Int
//    ) where T : Screen, T : IHoldScreen {
//        if (
//            action == InputConstants.RELEASE &&
//            key == holdScreen.keyCheck.key &&
//            modifierMatches(modifiers, holdScreen.keyCheck.keyModifier)
//        ) holdScreen.onClose()
//    }

//    private fun handleInput(
//        action: Int,
//        key: InputConstants.Key,
//        modifiers: Int,
//        player: LocalPlayer?,
//        screen: Screen?
//    ) {
//        if (action == InputConstants.REPEAT) return
//        if (screen is IHoldScreen) {
//            handleHoldScreenInput(screen, key, action, modifiers)
//        } else if (player != null && screen == null) {
//            val stackHeld = player.mainHandItem
//            val itemHeld = stackHeld.item
//
//            if (itemHeld is ToolGunItem) handleToolgunInput(
//                player,
//                itemHeld, stackHeld,
//                key, modifiers,
//                action == InputConstants.PRESS
//            )
//        }
//    }

//    @SubscribeEvent
//    fun keyInput(event: InputEvent.Key) {
//        handleInput(
//            event.action, InputConstants.getKey(event.key, event.scanCode), event.modifiers,
//            rgMinecraft.player, rgMinecraft.screen
//        )
//    }
//
//    @SubscribeEvent
//    fun mouseInput(event: InputEvent.MouseButton.Post) {
//        handleInput(
//            event.action, InputConstants.Type.MOUSE.getOrCreate(event.button), event.modifiers,
//            rgMinecraft.player, rgMinecraft.screen
//        )
//    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun login(event: PlayerEvent.PlayerLoggedInEvent) {
//        rgMinecraft.options.keyMappings = ArrayUtils.removeElements(
//            rgMinecraft.options.keyMappings,
//            openGuiEditor
//        )
        PhysXTestTool.createPhysX()
    }

//    private var createdMappings = listOf<KeyMapping>()

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun logout(event: PlayerEvent.PlayerLoggedOutEvent) {
//        rgMinecraft.options.keyMappings = ArrayUtils.removeElements(
//            rgMinecraft.options.keyMappings,
//            *createdMappings.toTypedArray()
//        )
        PhysXTestTool.destroyPhysX()
    }

    @SubscribeEvent
    fun clientTick(event: ClientTickEvent.Pre) {
        if (machTrailMap.isNotEmpty()) {
            machTrailMap.forEach { (_, machTrailData) ->
                machTrailData.tick()
                if (!machTrailData.player.isSprinting) {
                    machTrailData.machFourSound.shouldLoop = false
                    rgMinecraft.soundManager.stop(machTrailData.machFourSound)
                    machTrailMap.remove(machTrailData.playerProfile)
                }
            }
        }
    }
}