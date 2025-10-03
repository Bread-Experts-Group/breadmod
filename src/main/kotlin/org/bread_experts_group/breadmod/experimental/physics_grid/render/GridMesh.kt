package org.bread_experts_group.breadmod.experimental.physics_grid.render

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.RandomSource
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

// todo transparency sorting
class GridMesh(private val grid: PhysicsGrid) {
	private var isCompiled: Boolean = false
	private val meshes: MutableMap<RenderType, MeshData> = mutableMapOf()
	val bufferBuilders: MutableMap<RenderType, BufferBuilder> = mutableMapOf()
	val vertexBuffers: MutableMap<RenderType, VertexBuffer> = mutableMapOf()

	fun getOrBeginBufferBuilder(renderType: RenderType): BufferBuilder =
		this.bufferBuilders.getOrPut(renderType) {
			val byteBufferBuilder = ByteBufferBuilder(renderType.bufferSize)
			BufferBuilder(byteBufferBuilder, renderType.mode, renderType.format)
		}

	fun getOrBeginVertexBuffer(renderType: RenderType): VertexBuffer =
		this.vertexBuffers.getOrPut(renderType) { VertexBuffer(VertexBuffer.Usage.STATIC) }

	fun close() {
		this.meshes.values.forEach(MeshData::close)
		this.vertexBuffers.values.forEach(VertexBuffer::close)
	}

	fun compile(poseStack: PoseStack) {
		if (this.isCompiled) return
		val dispatcher = localClient.blockRenderer
		val modelBlockRenderer = dispatcher.modelRenderer
		val random = RandomSource.create()
		val level = localClient.level ?: return

		this.grid.blocks.forEach { (pos, pair) ->
			val state = pair.second
			val bakedModel = dispatcher.getBlockModel(state)
			poseStack.pushPose()
			poseStack.translate(pos)
			for (renderType in bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
				val builder = this.getOrBeginBufferBuilder(renderType)
				try {
					modelBlockRenderer.tesselateBlock(
						level,
						bakedModel,
						state,
						pos.above(255),
						poseStack,
						builder,
						true,
						random,
						state.getSeed(pos),
						OverlayTexture.NO_OVERLAY,
						bakedModel.getModelData(level, pos, state, ModelData.EMPTY),
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