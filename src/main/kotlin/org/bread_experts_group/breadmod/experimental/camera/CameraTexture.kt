package org.bread_experts_group.breadmod.experimental.camera

import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.Tickable
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.client.render.gamePaused
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.toVec3
import org.joml.Matrix4f

class CameraTexture(
	private val blockEntity: BreadModBlockEntity,
	val location: ResourceLocation
) : AbstractTexture(), Tickable {
	val frameBuffer: MainTarget = MainTarget(854, 480)
	private val camera: DummyCamera = DummyCamera()

	init {
		this.id = this.frameBuffer.colorTextureId
		executeOnRenderThread {
			localClient.textureManager.register(this.location, this)
			this.redraw()
		}
	}

	override fun getId(): Int = this.frameBuffer.colorTextureId

	override fun releaseId() {
		executeOnRenderThread {
			this.id = -1
			this.frameBuffer.destroyBuffers()
		}
	}

	override fun close() {
		logDebugInfo("CLOSING TEXTURE")
		this.releaseId()
		executeOnRenderThread { localClient.textureManager.release(this.location) }
	}

	override fun load(resourceManager: ResourceManager) {
	}

	override fun tick() {
		logDebugInfo("ticking?")
		executeOnRenderThread { this.redraw() }
	}

	fun redraw() {
		if (!localClient.isGameLoadFinished || localClient.level == null) return
		if (localClient.gamePaused()) return

		this.bind()
		this.setupCamera()

		CameraStuff.CAMERA_TARGET.bindWrite(true)

		RenderSystem.clear(16640, Minecraft.ON_OSX)
		FogRenderer.setupNoFog()
		RenderSystem.enableCull()

		CameraStuff.renderLevel(localClient, CameraStuff.CAMERA_TARGET, this.camera)

		this.dumpToFrameBuffer()
		localClient.mainRenderTarget.bindWrite(true)
	}

	fun setupCamera() {
		val level = this.blockEntity.level ?: return
		val (x, y, z) = this.blockEntity.blockPos.above().toVec3()
		if (this.camera.entity == null) this.camera.entity = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)
		this.camera.entity.setPos(x, y, z)
		this.camera.setPosition(x, y, z)
	}

	fun dumpToFrameBuffer() {
		this.frameBuffer.clear(true)
		this.frameBuffer.bindWrite(true)

		RenderSystem.getModelViewMatrix().set(Matrix4f().identity())
		RenderSystem.getProjectionMatrix().set(Matrix4f().identity())
		val buffer = localClient.renderBuffers().bufferSource()
		val renderType = CameraStuff.CAMERA_RENDER_TYPE.apply(CameraStuff.CAMERA_TARGET)
		val consumer = buffer.getBuffer(renderType)

		consumer.addVertex(-1f, -1f, 0f).setUv(0f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, -1f, 0f).setUv(1f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, 1f, 0f).setUv(1f, 0f).setColor(Color.WHITE)
		consumer.addVertex(-1f, 1f, 0f).setUv(0f, 0f).setColor(Color.WHITE)
		buffer.endBatch(renderType)
	}
}