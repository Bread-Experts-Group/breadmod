package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translate

object VertexThing {
	val vertexBuffer: VertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)
	val byteBufferBuilder: ByteBufferBuilder = ByteBufferBuilder(RenderType.solid().bufferSize)
	var meshData: MeshData? = null
	val renderType: RenderType = RenderType.solid()
	var generated: Boolean = false

	fun generate(event: RenderLevelStageEvent, blocks: Map<BlockPos, BlockState>) {
		if (this.generated) return
		val dispatcher = Minecraft.getInstance().blockRenderer
		val modelBlockRenderer = dispatcher.modelRenderer
		val random = RandomSource.create()
		val level = localClient.level ?: return
		val poseStack = event.poseStack

		this.byteBufferBuilder.clear()
		val builder = BufferBuilder(this.byteBufferBuilder, this.renderType.mode, this.renderType.format)

		blocks.forEach { (pos, state) ->
			val bakedModel = dispatcher.getBlockModel(state)
			poseStack.pushPose()
			poseStack.translate(pos)
			for (renderType in bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
				try {
					modelBlockRenderer.tesselateBlock(
						level,
						bakedModel,
						state,
						pos,
						poseStack,
						builder,
						false,
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

		this.meshData = builder.build()
		this.vertexBuffer.bind()
		this.vertexBuffer.upload(this.meshData!!)
		VertexBuffer.unbind()
		this.generated = true
	}
}