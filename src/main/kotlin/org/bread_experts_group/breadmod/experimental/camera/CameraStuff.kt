package org.bread_experts_group.breadmod.experimental.camera

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.NO_CULL
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import org.joml.Matrix4f
import org.joml.Quaternionf
import java.util.function.Function

object CameraStuff {
	val CAMERA_TARGET: TextureTarget = TextureTarget(854, 480, true, Minecraft.ON_OSX)
	val CAMERA_RENDER_TYPE: Function<RenderTarget, RenderType> = Util.memoize { renderTarget ->
		val textureState = RenderStateShard.TexturingStateShard("camera_draw", {
			RenderSystem.setShaderTexture(0, renderTarget.colorTextureId)
		}, {})

		RenderType.create(
			"test",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			false,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
				.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setTexturingState(textureState)
				.createCompositeState(false)
		)
	}

	private fun createProjectionMatrix(gameRenderer: GameRenderer, target: RenderTarget, fov: Float): Matrix4f =
		Matrix4f().perspective(
			fov * Mth.DEG_TO_RAD,
			target.width.toFloat() / target.height.toFloat(),
			0.05F,
			gameRenderer.depthFar
		)

	fun renderLevel(mc: Minecraft, target: RenderTarget, camera: Camera) {
		val matrix = this.createProjectionMatrix(mc.gameRenderer, target, 120f)
		matrix.mul(PoseStack().last().pose())
		mc.gameRenderer.resetProjectionMatrix(matrix)
		val cameraRot = camera.rotation().conjugate(Quaternionf())
		val cameraMatrix = Matrix4f().rotation(cameraRot)
		mc.levelRenderer.prepareCullFrustum(camera.position, cameraMatrix, matrix)
		mc.levelRenderer.renderLevel(
			mc.timer,
			false,
			camera,
			mc.gameRenderer,
			mc.gameRenderer.lightTexture(),
			cameraMatrix,
			matrix
		)
	}
}