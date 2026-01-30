package org.bread_experts_group.breadmod.experimental.physics_grid.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.CompilableMesh
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import org.bread_experts_group.breadmod.util.minus
import org.joml.Matrix4f
import java.util.Optional

// todo cutout and translucent occluding clouds & certain BER features like special effects, look into LevelRenderer..
class GridMesh(private val grid: PhysicsGrid) : CompilableMesh() {
	companion object {
		val meshes: MutableMap<PhysicsGrid, GridMesh> = mutableMapOf()
		fun create(grid: PhysicsGrid) {
			val mesh = GridMesh(grid)
			this.meshes[grid] = mesh
			mesh.attachRenderer()
		}
	}

	private var sortState: Optional<MeshData.SortState> = Optional.empty()

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
			val timer = event.partialTick
			val partialTick = timer.getGameTimeDeltaPartialTick(runsNormally)
			val pos = this.grid.getPosLerped(partialTick)
			val bufferSource = localClient.renderBuffers().bufferSource()
			val gridMesh = Companion.meshes[this.grid] ?: return@add true
			val poseStack = event.poseStack
			val camera = event.camera
			val modelViewMatrix = event.modelViewMatrix
			val projectionMatrix = event.projectionMatrix
			this.compile(poseStack, event.camera)
			// Rendering the grid's blocks
			this.render(RenderType.solid(), poseStack, camera, modelViewMatrix, projectionMatrix, timer)
			this.render(RenderType.cutoutMipped(), poseStack, camera, modelViewMatrix, projectionMatrix, timer)
			this.render(RenderType.cutout(), poseStack, camera, modelViewMatrix, projectionMatrix, timer)
			this.render(RenderType.translucent(), poseStack, camera, modelViewMatrix, projectionMatrix, timer)
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

	private fun sortIfNeeded(buffer: VertexBuffer, renderType: RenderType) {
		if (renderType != RenderType.translucent()) return
		val result = this.getSortResult() ?: return
		buffer.bind()
		buffer.uploadIndexBuffer(result)
		VertexBuffer.unbind()
	}

	override fun render(
		renderType: RenderType,
		poseStack: PoseStack,
		camera: Camera,
		modelViewMatrix: Matrix4f,
		projectionMatrix: Matrix4f,
		deltaTracker: DeltaTracker
	) {
		val partialTick = deltaTracker.getGameTimeDeltaPartialTick(false)
		val pos = this.grid.getPosLerped(partialTick)
		val buffer = this.vertexBuffers[renderType] ?: return
		this.sortIfNeeded(buffer, renderType)
		val shaderInstance = RenderSystem.getShader() ?: return
		poseStack.pushPose()
		poseStack.mulPose(modelViewMatrix)
		poseStack.offsetRenderToCameraPos(pos, camera, false)
		buffer.bind()
		buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance)
		VertexBuffer.unbind()
		poseStack.popPose()
	}

	override fun compile(poseStack: PoseStack, camera: Camera) {
		if (this.isCompiled) return
		val dispatcher = localClient.blockRenderer
		val modelBlockRenderer = dispatcher.modelRenderer
		val random = RandomSource.create()
		val blocks = (this.grid.microLevel.getChunk(0, 0) as ClientMicroLevelChunk).blocks
		blocks.forEach { (pos, state) ->
			val blockPos = pos.toBlockPos()
			val bakedModel = dispatcher.getBlockModel(state)
			poseStack.pushPose()
			poseStack.translate(blockPos)
			for (renderType in bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
				val builder = this.getOrBeginBufferBuilder(renderType)
				try {
					if (state.renderShape == RenderShape.INVISIBLE || state.renderShape == RenderShape.ENTITYBLOCK_ANIMATED) continue
					modelBlockRenderer.tesselateBlock(
						this.grid.microLevel,
						bakedModel,
						state,
						blockPos.above(255),
						poseStack,
						builder,
						true,
						random,
						state.getSeed(blockPos),
						OverlayTexture.NO_OVERLAY,
						bakedModel.getModelData(this.grid.microLevel, blockPos, state, ModelData.EMPTY),
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
		this.isCompiled = true
	}
}