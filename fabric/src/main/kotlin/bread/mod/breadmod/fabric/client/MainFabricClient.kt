package bread.mod.breadmod.fabric.client

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.AbstractModGuiOverlay
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.util.render.redness
import bread.mod.breadmod.util.render.rgMinecraft
import bread.mod.breadmod.util.render.skyColorMixinActive
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.Util
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.lang.Math.clamp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class MainFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        // todo gotta figure out how to make the vertexes render behind all terrain
        //  since pose stack isn't available until after terrain
        WorldRenderEvents.START.register { event ->
            if (WarOverlay.timerActive) {
                val entityPos = event.camera().entity.position()
                val poseStack = PoseStack()
                val tesselator = Tesselator.getInstance()
                val bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
                val millis = Util.getMillis()


                RenderSystem.depthMask(false)
                RenderSystem.setShader { GameRenderer.getPositionColorShader() }
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                RenderSystem.enableBlend()
                poseStack.pushPose()
                poseStack.translate(entityPos.x, entityPos.y, entityPos.z)
                val matrix = poseStack.last().pose()
                bufferBuilder.addVertex(matrix, 0f, 100f, 0f).setColor(0.9f, 0f, 0.1f, Mth.clamp(redness - 0.2f, 0f, 1f))

                for (j: Int in 0..16) {
                    val f1 = j * (Math.PI.toFloat() * 2f) / 16f
                    val f2: Float = sin(f1)
                    val f3: Float = cos(f1)
                    bufferBuilder.addVertex(matrix, f2, -1f, -f3).setColor(0.9f, 0f, 0.1f, Mth.clamp(redness - 0.2f, 0f, 1f))
                }

                val shaderFogColor = RenderSystem.getShaderFogColor()
                RenderSystem.setShaderFogColor(
                    shaderFogColor[0] + redness,
                    shaderFogColor[1] - redness,
                    shaderFogColor[2] - redness,
                    1f
                )
                FogRenderer.setupFog(
                    event.camera(),
                    FogRenderer.FogMode.FOG_SKY,
                    256f,
                    true,
                    event.tickCounter().realtimeDeltaTicks
                )
                FogRenderer.setupFog(
                    event.camera(),
                    FogRenderer.FogMode.FOG_TERRAIN,
                    max(256f, 32f),
                    true,
                    event.tickCounter().realtimeDeltaTicks
                )

                redness = clamp((sin(millis.toFloat() / 1800) + 1) / 2, 0f, 1f)
                skyColorMixinActive = true

                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
                RenderSystem.disableBlend()
                RenderSystem.depthMask(false)
                poseStack.popPose()
            } else if (!WarOverlay.timerActive) {
                redness = 0f
                skyColorMixinActive = false
            }
        }

        registerOverlay(modLocation("war_timer_overlay"), WarOverlay())
    }

    private fun registerOverlay(id: ResourceLocation, overlay: AbstractModGuiOverlay) =
        HudRenderCallback.EVENT.register(id, OverlayWrapper(overlay))

    private class OverlayWrapper(
        val overlay: AbstractModGuiOverlay
    ): HudRenderCallback {
        override fun onHudRender(
            drawContext: GuiGraphics,
            tickCounter: DeltaTracker
        ) {
            val gameWindow = rgMinecraft.window ?: return
            val screenWidth = gameWindow.guiScaledWidth
            val screenHeight = gameWindow.guiScaledHeight
            val poseStack = drawContext.pose()
            val player = rgMinecraft.player ?: return
            val buffer = drawContext.bufferSource()
            val partialTick = tickCounter.realtimeDeltaTicks

            overlay.renderOverlay(drawContext, partialTick, tickCounter, screenWidth, screenHeight, poseStack, buffer, player)
        }
    }
}
