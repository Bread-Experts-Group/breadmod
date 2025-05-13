package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.util.Mth.clamp
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

object WarRenderer {
	fun render(event: RenderLevelStageEvent) {
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_SKY && WarOverlay.timerActive) {
			val poseStack = event.poseStack
			val bufferBuilder =
				Tesselator.getInstance()
					.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
			val millis = Util.getMillis()

			RenderSystem.setShader(GameRenderer::getPositionColorShader)
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
			RenderSystem.enableBlend()
			poseStack.pushPose()
			poseStack.mulPose(Axis.XP.rotationDegrees(-17f))
			val matrix = poseStack.last().pose()
			val alpha = (clamp(redness - 0.2f, 0f, 1f) * 255).roundToInt()
			bufferBuilder
				.addVertex(matrix, 0f, 100f, 0f)
				.setColor(230, 0, 26, alpha)

			for (j: Int in 0 .. 16) {
				val f1 = j * (Math.PI.toFloat() * 2f) / 16f
				val f2: Float = sin(f1)
				val f3: Float = cos(f1)
				bufferBuilder
					.addVertex(matrix, f2, -1f, -f3)
					.setColor(230, 0, 26, alpha)
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
	}
}