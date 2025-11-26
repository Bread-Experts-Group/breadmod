package org.bread_experts_group.breadmod.experimental.mirror

import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.camera_viewer.DummyCamera
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.joml.Matrix4f
import org.joml.Quaternionf

class MirrorTexture(
	val location: ResourceLocation
) : AbstractTexture() {
	companion object {
		val camera: DummyCamera = DummyCamera()
		val textureTarget: TextureTarget = TextureTarget(400, 400, true, Minecraft.ON_OSX)
		var counter: Int = 0
	}

	val frameBuffer: MainTarget = MainTarget(400, 400)

	init {
		this.id = this.frameBuffer.colorTextureId
		executeOnRenderThread {
			localClient.textureManager.register(this.location, this)
		}
	}

	fun createProjectionMatrix(gameRenderer: GameRenderer, target: RenderTarget, fov: Float): Matrix4f =
		Matrix4f().perspective(
			fov * Mth.DEG_TO_RAD,
			(target.width / target.height).toFloat(),
			0.05F,
			gameRenderer.depthFar
		)

	fun setupCamera(blockEntity: BreadModBlockEntity) {
		val rotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
		val (x, y, z) = blockEntity.blockPos.center
		Companion.camera.setPosition(x, y, z)
		Companion.camera.setRotation(rotation, 0f)
	}

	fun writeToFrameBuffer(target: RenderTarget) {
		this.frameBuffer.clear(true)
		this.frameBuffer.bindWrite(true)

		RenderSystem.getModelViewMatrix().set(Matrix4f().identity())
		RenderSystem.getProjectionMatrix().set(Matrix4f().identity())
		val buffer = localClient.renderBuffers().bufferSource()
		val renderType = ModRenderType.renderTarget(target)
		val consumer = buffer.getBuffer(renderType)

		consumer.addVertex(-1f, -1f, 0f).setUv(0f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, -1f, 0f).setUv(1f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, 1f, 0f).setUv(1f, 0f).setColor(Color.WHITE)
		consumer.addVertex(-1f, 1f, 0f).setUv(0f, 0f).setColor(Color.WHITE)
		buffer.endBatch(renderType)
	}

	fun renderLevel(target: RenderTarget) {
		val camera = Companion.camera
		val mc = localClient
		val projectionMatrix = this.createProjectionMatrix(mc.gameRenderer, target, 70f)
		val poseStack = PoseStack()
		projectionMatrix.mul(poseStack.last().pose())
		mc.gameRenderer.resetProjectionMatrix(projectionMatrix)
		val cameraRotation = camera.rotation().conjugate(Quaternionf())
		val frustumMatrix = Matrix4f().rotation(cameraRotation)
		mc.levelRenderer.prepareCullFrustum(camera.position, frustumMatrix, projectionMatrix)
		mc.levelRenderer.renderLevel(
			mc.timer,
			false,
			camera,
			mc.gameRenderer,
			mc.gameRenderer.lightTexture(),
			frustumMatrix,
			projectionMatrix
		)
	}

	override fun load(resourceManager: ResourceManager) {
	}
}