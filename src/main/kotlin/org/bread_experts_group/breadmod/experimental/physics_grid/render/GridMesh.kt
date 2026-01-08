package org.bread_experts_group.breadmod.experimental.physics_grid.render

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.RenderShape
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos

// todo transparency sorting
class GridMesh(private val grid: PhysicsGrid) {
	private var isCompiled: Boolean = false
	private val meshes: MutableMap<RenderType, MeshData> = mutableMapOf()
	private val bufferBuilders: MutableMap<RenderType, BufferBuilder> = mutableMapOf()
	private val vertexBuffers: MutableMap<RenderType, VertexBuffer> = mutableMapOf()

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

	fun getBuffers(): Collection<VertexBuffer> = this.vertexBuffers.values

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
			val buffer = this.getOrBeginVertexBuffer(renderType)
			buffer.bind()
			buffer.upload(mesh)
			VertexBuffer.unbind()
		}
		this.isCompiled = true
	}
}