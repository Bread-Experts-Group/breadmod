package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.PositionalRandomFactory
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem.BulkBlockData
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import org.bread_experts_group.breadmod.util.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i

object BulkBlockBufferTask {
	val bufferSource: MultiBufferSource.BufferSource = localClient.renderBuffers().bufferSource()
	val modelData: ModelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()
	fun create(originPos: Vec3, blockData: BulkBlockData) {
		val blockRenderer = localClient.blockRenderer

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val rotation = passthrough[0] as Float
				val poseStack = event.poseStack
				val camera = event.camera
				val level = localClient.level ?: return@add true
				val frustum = event.frustum

				if (this.shouldRender(camera.position, originPos)) {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(originPos, camera, false)
					blockData.blocks.forEach { (offset, data) ->
						val proximal = BlockPos.containing(originPos + offset)
						if (!frustum.isVisible(AABB(proximal).inflate(0.7))) return@forEach
						val (state, packedLight, blockEntityData, _) = data
						poseStack.pushPose()
						poseStack.translate(offset)
						when (state.renderShape ?: return@add true) {
							INVISIBLE            -> throw IllegalStateException("Bad set! Had invisible state")
							ENTITYBLOCK_ANIMATED -> {}
							MODEL                -> this.tessellateBlockTest(
								data,
								BlockPos.containing(offset),
								level,
								poseStack
							)
						}
						blockEntityData?.let { (entity, renderer) ->
							renderer.render(
								entity,
								event.partialTick.gameTimeDeltaTicks,
								poseStack,
								this.bufferSource,
								packedLight,
								NO_OVERLAY
							)
						}
						poseStack.popPose()
					}
					blockData.fluids.forEach { (_, fluid) ->
						val (_, blockState, _) = fluid
						val model = blockRenderer.getBlockModel(blockState)
						model.getRenderTypes(blockState, NullRandom, this.modelData).forEach {
							println(it.name)
							blockRenderer.renderSingleBlock(
								blockState,
								poseStack,
								this.bufferSource,
								FULL_BRIGHT,
								NO_OVERLAY,
								this.modelData,
								it
							)
						}
					}
					poseStack.popPose()
					passthrough[0] = rotation + 5f * event.partialTick.gameTimeDeltaTicks
				}
				val stack = getStackInPlayerHand(localClient.player)
				!stack.`is`(ModItems.BULK_BLOCK_ITEM)
			},
			mutableListOf(
				0f
			)
		)
	}

	fun shouldRender(cameraPos: Vec3, originPos: Vec3): Boolean =
		Vec3.atCenterOf(originPos.toVec3i()).closerThan(cameraPos, this.getViewDistance())

	fun getViewDistance(): Double = 256.0

	object NullRandom : RandomSource {
		override fun fork(): RandomSource = NullRandom
		override fun forkPositional(): PositionalRandomFactory = object : PositionalRandomFactory {
			override fun fromHashOf(p0: String): RandomSource = NullRandom
			override fun fromSeed(p0: Long): RandomSource = NullRandom
			override fun at(p0: Int, p1: Int, p2: Int): RandomSource = NullRandom
			override fun parityConfigString(p0: StringBuilder) {}
		}

		override fun setSeed(p0: Long) {}
		override fun nextInt(): Int = 0
		override fun nextInt(p0: Int): Int = 0
		override fun nextLong(): Long = 0
		override fun nextBoolean(): Boolean = false
		override fun nextFloat(): Float = 0f
		override fun nextDouble(): Double = 0.0
		override fun nextGaussian(): Double = 1.0
	}

	fun tessellateBlockTest(
		data: BulkBlockItem.BlockData,
		pos: BlockPos,
		level: BlockAndTintGetter,
		poseStack: PoseStack
	) {
//		val model = localClient.modelManager.blockModelShaper.getBlockModel(data.state)
//		val ambientOcclusionFlag =
//			Minecraft.useAmbientOcclusion() && when (model.useAmbientOcclusion(data.state, modelData, renderType)) {
//				TRUE    -> true
//				DEFAULT -> data.state.getLightEmission(level, pos) == 0
//				FALSE   -> false
//			}
		data.ao.forEach { (_, types) ->
			types.forEach { (type, quads) ->
				quads.forEach { (quad, ao) ->
					this.putQuadData(
						level,
						data.state,
						pos,
						this.bufferSource.getBuffer(type),
						poseStack.last(),
						quad,
						ao.brightness,
						ao.lightmap
					)
				}
			}
		}

		poseStack.translate(data.state.getOffset(level, pos))
	}

	private fun putQuadData(
		level: BlockAndTintGetter,
		state: BlockState,
		pos: BlockPos,
		consumer: VertexConsumer,
		pose: PoseStack.Pose,
		quad: BakedQuad,
		brightness: FloatArray,
		lightmap: IntArray
	) {
		var red = 1f
		var green = 1f
		var blue = 1f
		if (quad.isTinted) {
			val i: Int = localClient.blockRenderer.modelRenderer.blockColors.getColor(
				state, level, pos,
				quad.tintIndex
			)
			red = (i shr 16 and 255).toFloat() / 255.0f
			green = (i shr 8 and 255).toFloat() / 255.0f
			blue = (i and 255).toFloat() / 255.0f
		}

		consumer.putBulkData(
			pose,
			quad,
			brightness,
			red,
			green,
			blue,
			1.0f,
			lightmap,
			NO_OVERLAY,
			true
		)
	}
}