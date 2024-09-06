package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.util.render.redness
import bread.mod.breadmod.client.WarTickerClient
import bread.mod.breadmod.util.render.skyColorMixinActive
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.util.Mth
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import java.lang.Math.clamp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object ClientForgeEventBus {

    @SubscribeEvent
    fun renderStageEvent(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_SKY && WarTickerClient.timerActive) {
            val poseStack = event.poseStack
            val tesselator = Tesselator.getInstance()
            val bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
            val millis = Util.getMillis()

            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            poseStack.pushPose()
            poseStack.mulPose(Axis.XP.rotationDegrees(-17f))
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
        } else if (!WarTickerClient.timerActive) {
            redness = 0f
            skyColorMixinActive = false
        }
    }
}