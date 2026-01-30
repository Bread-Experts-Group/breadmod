package org.bread_experts_group.breadmod.experimental.lidar

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import org.bread_experts_group.breadmod.client.render.CompilableMesh
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarSection
import org.bread_experts_group.breadmod.registry.shader.ModPostChains
import org.joml.Matrix4f

class SectionMesh(private val section: LidarSection) : CompilableMesh() {
	override fun render(
		poseStack: PoseStack,
		camera: Camera,
		modelViewMatrix: Matrix4f,
		projectionMatrix: Matrix4f,
		deltaTracker: DeltaTracker
	) {
		val buffer = this.getOrBeginVertexBuffer(RenderType.debugQuads())
		val shaderInstance = GameRenderer.getPositionColorShader() ?: return
		poseStack.pushPose()
		poseStack.mulPose(modelViewMatrix)
		poseStack.initialTranslate(camera)
		ModPostChains.lidarTarget.bindWrite(false)
		buffer.bind()
		buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance)
		VertexBuffer.unbind()
		localClient.mainRenderTarget.bindWrite(false)
		poseStack.popPose()
	}

	override fun compile(poseStack: PoseStack, camera: Camera) {
		if (this.isCompiled) return
		val bufferBuilder = this.getOrBeginBufferBuilder(RenderType.debugQuads())
		val vertexBuffer = this.getOrBeginVertexBuffer(RenderType.debugQuads())
		this.section.lidarBlocks.forEach { (_, block) ->
			poseStack.pushPose()
			block.render(poseStack, camera, bufferBuilder, true)
			poseStack.popPose()
		}
		val mesh = this.meshes.getOrPut(RenderType.debugQuads()) { bufferBuilder.buildOrThrow() }
		vertexBuffer.bind()
		vertexBuffer.upload(mesh)
		VertexBuffer.unbind()
		this.isCompiled = true
	}
}