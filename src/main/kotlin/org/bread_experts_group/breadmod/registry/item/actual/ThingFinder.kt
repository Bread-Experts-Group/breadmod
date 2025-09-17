package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.MeshData.SortState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import org.joml.Matrix4f
import org.joml.Vector3f

// code from JustDireThings (pending destruction)
object ThingFinder {
	var oreBlocksList: List<BlockPos> = listOf()
	private var sortCounter: Int = 0

	//A eBufferBuilder, so we can draw the render
	private val byteBufferBuilder: ByteBufferBuilder = ByteBufferBuilder(RenderType.cutout().bufferSize())

	//Cached SortStates used for re-sorting every so often
	private var meshdata: MeshData? = null
	private var sortState: SortState? = null

	//Vertex Buffer to buffer the different ores.
	private val vertexBuffer: VertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)

	//The render type
	private val renderType: RenderType = RenderType.translucent()
	private var renderedAtPos: BlockPos = BlockPos.ZERO

	fun render(evt: RenderLevelStageEvent, player: Player) {
		this.drawVBO(evt, player)
	}

	fun generateVBO(player: Player) {
		if (this.oreBlocksList.isEmpty()) return
		val matrix = PoseStack() //Create a new matrix stack for use in the buffer building process
		val dispatcher = Minecraft.getInstance().blockRenderer
		val modelBlockRenderer = dispatcher.modelRenderer
		val random = RandomSource.create()
		val level = player.level()
		this.renderedAtPos = player.onPos

		this.byteBufferBuilder.clear()
		val builder = BufferBuilder(this.byteBufferBuilder, this.renderType.mode(), this.renderType.format())

		for (pos in this.oreBlocksList) {
			val renderState = level.getBlockState(pos)
			if (renderState.isAir) continue
			val bakedModel = dispatcher.getBlockModel(renderState)
			matrix.pushPose()
			matrix.translate(
				-this.renderedAtPos.x.toFloat(),
				-this.renderedAtPos.y.toFloat(),
				-this.renderedAtPos.z.toFloat()
			)
			matrix.translate(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
			//We make this just a TINY bit smaller than a full block - because we're doing GREATERTHAN depth testing.
			val translateF = 1f / 2000
			matrix.translate(translateF, translateF, translateF)
			val scaleF = 1f / 1000
			matrix.scale(1 - scaleF, 1 - scaleF, 1 - scaleF)

			for (renderTypeDraw in bakedModel.getRenderTypes(renderState, random, ModelData.EMPTY)) {
				try {
					modelBlockRenderer.tesselateBlock(
						level,
						bakedModel,
						renderState,
						pos.above(255),
						matrix,
						builder,
						false,
						random,
						renderState.getSeed(pos),
						OverlayTexture.NO_OVERLAY,
						bakedModel.getModelData(level, pos, renderState, ModelData.EMPTY),
						renderTypeDraw
					)
				} catch (e: Exception) {
					//System.out.println(e);
				}
			}
			matrix.popPose()
		}
		//Sort all the builder's vertices and then upload them to the vertex buffer
		val projectedView = Minecraft.getInstance().gameRenderer.mainCamera.position
		val subtracted = projectedView.subtract(
			this.renderedAtPos.x.toDouble(),
			this.renderedAtPos.y.toDouble(),
			this.renderedAtPos.z.toDouble()
		)
		val sortPos = Vector3f(subtracted.x.toFloat(), subtracted.y.toFloat(), subtracted.z.toFloat())
		if (this.meshdata != null) this.meshdata!!.close()
		this.meshdata = builder.build()
		if (this.meshdata != null) {
			this.sortState = this.meshdata!!.sortQuads(this.byteBufferBuilder, VertexSorting.byDistance(sortPos))
			this.vertexBuffer.bind()
			this.vertexBuffer.upload(this.meshdata!!)
			VertexBuffer.unbind()
		}
		this.oreBlocksList = listOf()
	}

	fun drawVBO(evt: RenderLevelStageEvent, player: Player) {
		val projectedView = Minecraft.getInstance().gameRenderer.mainCamera.position
		val currentPos = player.onPos
		val renderPos = BlockPos(
			currentPos.x - (currentPos.x - this.renderedAtPos.x),
			currentPos.y - (currentPos.y - this.renderedAtPos.y),
			currentPos.z - (currentPos.z - this.renderedAtPos.z)
		)
		//Sort every <X> Frames to prevent screendoor effect
		if (this.sortCounter > 20) {
			if (this.sortState != null) this.sortAll(renderPos)
			this.sortCounter = 0
		} else {
			this.sortCounter++
		}
		val matrix = evt.poseStack
		matrix.pushPose()
		matrix.mulPose(evt.modelViewMatrix)
		matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z())
		matrix.translate(renderPos.x.toFloat(), renderPos.y.toFloat(), renderPos.z.toFloat())
		//Draw the renders in the specified order
		try {
			if (this.vertexBuffer.format == null) return  //IDE says this is never null, but if we remove this check we crash because its null so....
			this.vertexBuffer.bind()
			this.vertexBuffer.drawWithShader(
				matrix.last().pose(),
				Matrix4f(evt.projectionMatrix),
				RenderSystem.getShader() ?: return
			)
			VertexBuffer.unbind()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		matrix.popPose()
	}

	fun sortAll(lookingAt: BlockPos) {
		val sortResult = this.sort(lookingAt)
		this.vertexBuffer.bind()
		this.vertexBuffer.uploadIndexBuffer(sortResult)
		VertexBuffer.unbind()
	}

	//Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
	fun sort(lookingAt: BlockPos): ByteBufferBuilder.Result {
		val projectedView = Minecraft.getInstance().gameRenderer.mainCamera.position
		val subtracted = projectedView.subtract(
			lookingAt.x.toDouble(),
			lookingAt.y.toDouble(),
			lookingAt.z.toDouble()
		)
		val sortPos = Vector3f(subtracted.x.toFloat(), subtracted.y.toFloat(), subtracted.z.toFloat())
		return this.sortState!!.buildSortedIndexBuffer(this.byteBufferBuilder, VertexSorting.byDistance(sortPos))!!
	}
}
