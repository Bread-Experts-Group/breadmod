package org.bread_experts_group.breadmod.experimental.camera_viewer

import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.Tickable
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Mth
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.client.render.gamePaused
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.mixinutil.General
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.joml.Matrix4f
import org.joml.Quaternionf

// todo separate framebuffer specific stuff into FrameBufferTexture and extend it into this
class CameraTexture(
	val pos: BlockPos,
	val width: Int,
	val height: Int,
	private val cameraEntity: BreadModBlockEntity,
	val location: ResourceLocation
) : AbstractTexture(), Tickable {
	companion object {
		val camera: DummyCamera = DummyCamera()
		@JvmField
		var targetBeingRendered: RenderTarget? = null
		private var textureCounter: Int = 0
		private val frameTargets: MutableMap<BlockPos, TextureTarget> = mutableMapOf()
		val textures: MutableMap<BlockPos, CameraTexture> = mutableMapOf()
		fun get(blockEntity: BreadModBlockEntity, width: Int, height: Int, shouldTick: Boolean): CameraTexture? {
			val level = blockEntity.level ?: return null
			val handler = blockEntity.getCapability(CameraViewerHandler.BLOCK_VOID)
			if (handler.boundPos == BlockPos.ZERO) return null
			if (!level.isAreaLoaded(handler.boundPos, 100)) return null
			val cameraEntity = level.getBlockEntity(handler.boundPos) as? BreadModBlockEntity ?: return null
			val texture = this.textures.getOrPut(handler.boundPos) {
				CameraTexture(
					handler.boundPos,
					width,
					height,
					cameraEntity,
					modLocation("camera_texture_${this.textureCounter++}")
				)
			}
			texture.shouldTick = shouldTick
			return texture
		}
	}

	val frameBuffer: MainTarget = MainTarget(this.width, this.height)
	var initialized: Boolean = false
	var shouldTick: Boolean = true

	fun init() {

		val level = this.cameraEntity.level ?: return
		if (General.textureLock) return
		if (Companion.camera.entity == null)
			Companion.camera.entity = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)

		this.id = this.frameBuffer.colorTextureId
		executeOnRenderThread {
			localClient.textureManager.register(this.location, this)
			this.updateTexture()
		}
		this.initialized = true
	}

	override fun getId(): Int = this.frameBuffer.colorTextureId

	override fun releaseId() {
		executeOnRenderThread {
			this.id = -1
			this.frameBuffer.destroyBuffers()
		}
	}

	override fun close() {
		this.initialized = false
		this.releaseId()
		executeOnRenderThread {
			localClient.textureManager.release(this.location)
			Companion.frameTargets.remove(this.pos)?.destroyBuffers()
		}
	}

	override fun load(resourceManager: ResourceManager) {
	}

	override fun tick() {
		if (!this.initialized) return
		if (!this.shouldTick) return
		executeOnRenderThread { this.updateTexture() }
	}

	private fun updateTexture() {
		val mc = localClient
		if (!mc.isGameLoadFinished || mc.level == null) return
		if (mc.gamePaused()) return
		val target = Companion.frameTargets.getOrPut(this.pos) {
			TextureTarget(this.width, this.height, true, Minecraft.ON_OSX)
		}
		val mainTarget = mc.mainRenderTarget

		this.bind()
		this.setupCamera(mc)

		target.bindWrite(true)
		Companion.targetBeingRendered = target

		RenderSystem.clear(16640, Minecraft.ON_OSX)
		FogRenderer.setupNoFog()
		RenderSystem.enableCull()
		val oldRenderDistance = mc.gameRenderer.renderDistance
		mc.gameRenderer.renderDistance = 512f
		this.renderLevel(mc, target, Companion.camera)
		mc.gameRenderer.renderDistance = oldRenderDistance
		this.writeToFrameBuffer(mc, target)
		Companion.targetBeingRendered = null
		mainTarget.bindWrite(true)
	}

	private fun setupCamera(mc: Minecraft) {
		val partialTick = mc.timer.gameTimeDeltaTicks
		val rotation = this.cameraEntity.getLerpTicker<Int>().getLerpedValue(0, partialTick)
		val (x, y, z) = this.cameraEntity.blockPos.center
		Companion.camera.setPosition(x, y, z)
		Companion.camera.setRotation(rotation, 0f)
	}

	private fun writeToFrameBuffer(mc: Minecraft, target: RenderTarget) {
		this.frameBuffer.clear(true)
		this.frameBuffer.bindWrite(true)

		RenderSystem.getModelViewMatrix().set(Matrix4f().identity())
		RenderSystem.getProjectionMatrix().set(Matrix4f().identity())
		val buffer = mc.renderBuffers().bufferSource()
		val renderType = ModRenderType.renderTarget(target)
		val consumer = buffer.getBuffer(renderType)

		consumer.addVertex(-1f, -1f, 0f).setUv(0f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, -1f, 0f).setUv(1f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, 1f, 0f).setUv(1f, 0f).setColor(Color.WHITE)
		consumer.addVertex(-1f, 1f, 0f).setUv(0f, 0f).setColor(Color.WHITE)
		buffer.endBatch(renderType)
	}

	private fun createProjectionMatrix(gameRenderer: GameRenderer, target: RenderTarget, fov: Float): Matrix4f =
		Matrix4f().perspective(
			fov * Mth.DEG_TO_RAD,
			(target.width / target.height).toFloat(),
			0.05F,
			gameRenderer.depthFar
		)

	private fun renderLevel(mc: Minecraft, target: RenderTarget, camera: DummyCamera) {
		val deltaTracker = mc.timer
		val gameRenderer = mc.gameRenderer
		val levelRenderer = mc.levelRenderer
		val projectionMatrix = this.createProjectionMatrix(gameRenderer, target, 70f)
		val poseStack = PoseStack()

		projectionMatrix.mul(poseStack.last().pose())
		gameRenderer.resetProjectionMatrix(projectionMatrix)
		val cameraRotation = camera.rotation().conjugate(Quaternionf())
		val frustumMatrix = Matrix4f().rotation(cameraRotation)
		levelRenderer.prepareCullFrustum(camera.position, frustumMatrix, projectionMatrix)
		levelRenderer.renderLevel(
			deltaTracker,
			false,
			camera,
			gameRenderer,
			gameRenderer.lightTexture(),
			frustumMatrix,
			projectionMatrix
		)

		ClientHooks.dispatchRenderStage(
			RenderLevelStageEvent.Stage.AFTER_LEVEL,
			levelRenderer,
			null,
			RenderSystem.getModelViewMatrix(),
			projectionMatrix,
			levelRenderer.ticks,
			camera,
			levelRenderer.frustum
		)
	}
}