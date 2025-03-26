package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.levelgen.PositionalRandomFactory
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import net.neoforged.neoforge.common.util.TriState.DEFAULT
import net.neoforged.neoforge.common.util.TriState.FALSE
import net.neoforged.neoforge.common.util.TriState.TRUE
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem.BulkBlockData
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import java.util.BitSet
import kotlin.jvm.optionals.getOrNull

object BulkBlockBufferTask {
	val modelData: ModelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()
	fun create(originPos: Vec3, blockData: BulkBlockData) {
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val rotation = passthrough[0] as Float
				val poseStack = event.poseStack
				val camera = event.camera
				val level = localClient.level ?: return@add true

				if (this.shouldRender(camera.position, originPos)) {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(originPos, camera, false)

					poseStack.translate(0.5, 0.5, 0.5)
					poseStack.translate(-blockData.aabbCenter)
//				    poseStack.mulPose(Axis.YN.rotationDegrees(rotation))
					poseStack.translate(blockData.aabbCenter)
					poseStack.translate(-0.5, -0.5, -0.5)

					blockData.blocks.forEach { (offset, data) ->
						val (state, packedLight, blockEntityData, ao) = data
						poseStack.pushPose()
						val model = blockRenderer.getBlockModel(state)
						poseStack.translate(offset)
//						this.rotateBlocks(state, poseStack)
						model.getRenderTypes(state, NullRandom, this.modelData).forEach {
							when (state.renderShape ?: return@add true) {
								INVISIBLE            -> throw IllegalStateException("Bad set! Had invisible state")
								ENTITYBLOCK_ANIMATED -> if (blockEntityData == null) blockRenderer.renderSingleBlock(
									state,
									poseStack,
									bufferSource,
									packedLight,
									NO_OVERLAY,
									this.modelData,
									it
								) else blockEntityData.let { (entity, renderer) ->
									renderer.render(
										entity,
										event.partialTick.gameTimeDeltaTicks,
										poseStack,
										bufferSource,
										packedLight,
										NO_OVERLAY
									)
								}
								MODEL                -> this.tessellateBlockTest(
									data,
									BlockPos.containing(offset),
									level,
									poseStack,
									bufferSource.getBuffer(it),
									this.modelData,
									it
								)
							}
						}
						poseStack.popPose()
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

	fun rotateBlocks(state: BlockState, poseStack: PoseStack) {
		val direction = state.getOptionalValue(HORIZONTAL_FACING) ?: state.getOptionalValue(FACING) ?: return
		val facing = direction.getOrNull()
		if (facing != null && state.renderShape == ENTITYBLOCK_ANIMATED) {
			poseStack.translate(0.5f, 0.5f, 0.5f)
			poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
			poseStack.translate(-0.5f, -0.5f, -0.5f)
		}
	}

	fun shouldRender(cameraPos: Vec3, originPos: Vec3): Boolean =
		Vec3.atCenterOf(originPos.toVec3i()).closerThan(cameraPos, this.getViewDistance())

	fun getViewDistance(): Double = 64.0

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
		poseStack: PoseStack,
		consumer: VertexConsumer,
		modelData: ModelData,
		renderType: RenderType,
		randomSource: RandomSource = NullRandom
	) {
		val model = localClient.modelManager.blockModelShaper.getBlockModel(data.state)
		val modelRenderer = localClient.blockRenderer.modelRenderer
		val ambientOcclusionFlag =
			Minecraft.useAmbientOcclusion() && when (model.useAmbientOcclusion(data.state, modelData, renderType)) {
				TRUE    -> true
				DEFAULT -> data.state.getLightEmission(level, pos) == 0
				FALSE   -> false
				else    -> false
			}
		val bitSet = BitSet(3)
		if (ambientOcclusionFlag) data.ao.forEach { (_, quads) ->
			quads.forEach { (quad, ao) ->
				this.putQuadData(
					level,
					data.state,
					pos,
					consumer,
					poseStack.last(),
					quad,
					ao.brightness,
					ao.lightmap
				)
			}
		} else repeat(Direction.entries.size + 1) {
			modelRenderer.renderModelFaceFlat(
				level,
				data.state,
				pos,
				0,
				NO_OVERLAY,
				true,
				poseStack,
				consumer,
				model.getQuads(
					data.state, Direction.entries.getOrNull(it), randomSource,
					modelData, renderType
				),
				bitSet
			)
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