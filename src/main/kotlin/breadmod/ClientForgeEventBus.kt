package breadmod

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.client.gui.WarTickerClient
import breadmod.commands.client.AltToolGunModelCommand
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.serverbound.tool_gun.ToolGunConfigurationPacket
import breadmod.util.gui.IHoldScreen
import breadmod.util.render.modifierMatches
import breadmod.util.render.renderBuffer
import breadmod.util.render.rgMinecraft
import breadmod.util.render.skyColorMixinActive
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.commands.Commands
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth.clamp
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
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

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

    var redness = 0f

    @SubscribeEvent
    fun renderStageEvent(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_SKY && WarTickerClient.timerActive) {
            val poseStack = event.poseStack
            val bufferBuilder = Tesselator.getInstance().builder
            val millis = Util.getMillis()

            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            poseStack.pushPose()
            poseStack.mulPose(Axis.XP.rotationDegrees(-17f))
            val matrix = poseStack.last().pose()
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
            bufferBuilder.vertex(matrix, 0f, 100f, 0f).color(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f)).endVertex()

            for (j: Int in 0..16) {
                val f1 = j * (Math.PI.toFloat() * 2f) / 16f
                val f2: Float = sin(f1)
                val f3: Float = cos(f1)
                bufferBuilder.vertex(matrix, f2, -1f, -f3).color(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f)).endVertex()
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
                event.partialTick
            )
            FogRenderer.setupFog(
                event.camera,
                FogRenderer.FogMode.FOG_TERRAIN,
                max(256f, 32f),
                true,
                event.partialTick
            )

            redness = clamp((sin(millis.toFloat() / 1800) + 1) / 2, 0f, 1f)
            skyColorMixinActive = true

            BufferUploader.drawWithShader(bufferBuilder.end())
            RenderSystem.disableBlend()
            poseStack.popPose()
        } else if (!WarTickerClient.timerActive) {
            redness = 0.0f
            skyColorMixinActive = false
        }
    }

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

    private fun handleInput(
        action: Int,
        key: InputConstants.Key,
        modifiers: Int,
        player: LocalPlayer?,
        screen: Screen?
    ) {
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
        event.dispatcher.register(Commands.literal("breadmod")
            .then(AltToolGunModelCommand.register())
        )
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return

        WarTickerClient.tick()
    }
}