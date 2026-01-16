package org.bread_experts_group.breadmod.experimental.physics_grid.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import org.bread_experts_group.breadmod.util.minus
import java.util.Optional

// todo cutout and translucent occluding clouds & certain BER features like special effects, look into LevelRenderer..
class GridMesh(private val grid: PhysicsGrid) {
	companion object {
		val meshes: MutableMap<PhysicsGrid, GridMesh> = mutableMapOf()
		fun create(grid: PhysicsGrid) {
			val mesh = GridMesh(grid)
			this.meshes[grid] = mesh
			mesh.attachRenderer()
		}
	}

	private var isCompiled: Boolean = false
	private val meshes: MutableMap<RenderType, MeshData> = mutableMapOf()
	private val bufferBuilders: MutableMap<RenderType, BufferBuilder> = mutableMapOf()
	private val vertexBuffers: MutableMap<RenderType, VertexBuffer> = mutableMapOf()
	private var sortState: Optional<MeshData.SortState> = Optional.empty()

	private fun getOrBeginBufferBuilder(renderType: RenderType): BufferBuilder =
		this.bufferBuilders.getOrPut(renderType) {
			val byteBufferBuilder = ByteBufferBuilder(renderType.bufferSize)
			BufferBuilder(byteBufferBuilder, renderType.mode, renderType.format)
		}

	private fun getOrBeginVertexBuffer(renderType: RenderType): VertexBuffer =
		this.vertexBuffers.getOrPut(renderType) { VertexBuffer(VertexBuffer.Usage.STATIC) }

	fun close() {
		this.meshes.values.forEach(MeshData::close)
		this.vertexBuffers.values.forEach(VertexBuffer::close)
	}

	fun recompile() {
		this.close()
		this.vertexBuffers.clear()
		this.bufferBuilders.clear()
		this.meshes.clear()
		this.isCompiled = false
	}

	private fun renderBuffer(
		renderType: RenderType,
		event: RenderLevelStageEvent,
		pos: Vec3
	) {
		val buffer = this.vertexBuffers[renderType] ?: return
		val poseStack = event.poseStack
		if (renderType == RenderType.translucent()) {
//			val translucentTarget = localClient.levelRenderer.translucentTarget ?: return
//			translucentTarget.clear(Minecraft.ON_OSX)
//			translucentTarget.copyDepthFrom(localClient.mainRenderTarget)
			val result = this.getSortResult()
			if (result != null) {
				buffer.bind()
				buffer.uploadIndexBuffer(result)
				VertexBuffer.unbind()
			}
		}
		val shaderInstance = RenderSystem.getShader() ?: return
		poseStack.pushPose()
		poseStack.mulPose(event.modelViewMatrix)
		poseStack.offsetRenderToCameraPos(pos, event.camera, false)
		buffer.bind()
		buffer.drawWithShader(
			poseStack.last().pose(),
			event.projectionMatrix,
			shaderInstance
		)
		shaderInstance.clear()
		VertexBuffer.unbind()
		poseStack.popPose()
	}

	fun compile(poseStack: PoseStack) {
		if (this.isCompiled) return
		val dispatcher = localClient.blockRenderer
		val modelBlockRenderer = dispatcher.modelRenderer
		val random = RandomSource.create()
		val level = localClient.level ?: return
		(this.grid.microLevel.getChunk(0, 0) as ClientMicroLevelChunk).blocks.forEach { (pos, state) ->
			val blockPos = pos.toBlockPos()
			val bakedModel = dispatcher.getBlockModel(state)
			poseStack.pushPose()
			poseStack.translate(blockPos)
			for (renderType in bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
				val builder = this.getOrBeginBufferBuilder(renderType)
				try {
					if (state.renderShape == RenderShape.INVISIBLE || state.renderShape == RenderShape.ENTITYBLOCK_ANIMATED) continue
					modelBlockRenderer.tesselateBlock(
						level,
						bakedModel,
						state,
						blockPos.above(255),
						poseStack,
						builder,
						true,
						random,
						state.getSeed(blockPos),
						OverlayTexture.NO_OVERLAY,
						bakedModel.getModelData(level, blockPos, state, ModelData.EMPTY),
						renderType
					)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
			poseStack.popPose()
		}

		this.bufferBuilders.forEach { (renderType, builder) ->
			val mesh = this.meshes.getOrPut(renderType) { builder.buildOrThrow() }
			if (renderType == RenderType.translucent()) {
				val sorting = this.createSorting()
				this.sortState = Optional.of(mesh.sortQuads(builder.buffer, sorting) ?: return@forEach)
			}
			val buffer = this.getOrBeginVertexBuffer(renderType)
			buffer.bind()
			buffer.upload(mesh)
			VertexBuffer.unbind()
		}
		this.vertexBuffers
		this.isCompiled = true
	}

	private fun createSorting(): VertexSorting {
		val camera = localClient.gameRenderer.mainCamera.position
		val pos = this.grid.pos
		val subtracted = camera.subtract(pos).toVector3f()
		return VertexSorting.byDistance(subtracted)
	}

	fun getSortResult(): ByteBufferBuilder.Result? {
		if (!this.sortState.isPresent) return null
		val state = this.sortState.get()
		val sorting = this.createSorting()
		val builder = this.bufferBuilders.getValue(RenderType.translucent()).buffer
		return state.buildSortedIndexBuffer(builder, sorting)
	}

	fun attachRenderer() {
		val renderBounding = AABB(
			this.grid.bounding.minPosition - this.grid.pos,
			this.grid.bounding.maxPosition - this.grid.pos
		)
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			val runsNormally = this.grid.microLevel.tickRateManager().runsNormally()
			val partialTick = event.partialTick.getGameTimeDeltaPartialTick(runsNormally)
			val pos = this.grid.getPosLerped(partialTick)
			val bufferSource = localClient.renderBuffers().bufferSource()
			val gridMesh = Companion.meshes[this.grid] ?: return@add true
			val poseStack = event.poseStack
			gridMesh.compile(poseStack)
			// Rendering the grid's blocks
			gridMesh.renderBuffer(RenderType.solid(), event, pos)
			gridMesh.renderBuffer(RenderType.cutoutMipped(), event, pos)
			gridMesh.renderBuffer(RenderType.cutout(), event, pos)
			gridMesh.renderBuffer(RenderType.translucent(), event, pos)
			//  Rendering block entities
			poseStack.pushPose()
			poseStack.offsetRenderToCameraPos(pos, event.camera, false)
			(this.grid.microLevel.getChunk(0, 0) as ClientMicroLevelChunk).blocks.forEach { (pos, _) ->
				val blockEntity = this.grid.microLevel.getBlockEntity(pos.toBlockPos()) ?: return@forEach
				poseStack.pushPose()
				poseStack.translate(pos.toBlockPos())
				localClient.blockEntityRenderDispatcher.getRenderer(blockEntity)?.render(
					blockEntity,
					partialTick,
					poseStack,
					bufferSource,
					LightTexture.FULL_BRIGHT,
					OverlayTexture.NO_OVERLAY
				)
				poseStack.popPose()
			}
			poseStack.popPose()
			// Rendering the grid's bounding box
			poseStack.pushPose()
			poseStack.offsetRenderToCameraPos(pos, event.camera, false)
			LevelRenderer.renderLineBox(
				poseStack,
				bufferSource.getBuffer(RenderType.lines()),
				renderBounding,
				1f,
				1f,
				1f,
				1f
			)
			// Removing the renderer if the grid doesn't exist anymore
			if (!PhysicsGrid.clientGrids.values.contains(this.grid)) {
				gridMesh.close()
				Companion.meshes.remove(this.grid)
				true
			} else false
		})
	}
}