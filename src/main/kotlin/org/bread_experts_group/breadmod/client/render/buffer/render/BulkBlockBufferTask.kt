package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem.BulkBlockData
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import kotlin.jvm.optionals.getOrNull

object BulkBlockBufferTask {
	fun create(originPos: Vec3, blockData: BulkBlockData) {
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer
		val entityRenderDispatcher = localClient.blockEntityRenderDispatcher
		val random = RandomSource.create(42)
		val modelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()

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

					blockData.blocks.forEach { (offset, state) ->
						poseStack.pushPose()
						val model = blockRenderer.getBlockModel(state)
						poseStack.translate(offset)
						this.rotateBlocks(state, poseStack)
						model.getRenderTypes(state, random, modelData).forEach {
							when (state.renderShape ?: return@add true) {
								INVISIBLE            -> {}
								ENTITYBLOCK_ANIMATED -> {
									blockRenderer.renderSingleBlock(
										state,
										poseStack,
										bufferSource,
										this.getLight(level, offset),
										NO_OVERLAY,
										modelData,
										it
									)
								}
								MODEL                -> {
									blockRenderer.renderBatched(
										state,
										BlockPos.containing(offset),
										level,
										poseStack,
										bufferSource.getBuffer(it),
										false,
										random,
										modelData,
										it
									)
								}
							}
						}
						poseStack.popPose()
					}
					blockData.blockEntities.forEach { (offset, entity) ->
						poseStack.pushPose()
						poseStack.translate(offset)
						this.rotateBlocks(entity.blockState, poseStack)
						val renderer = entityRenderDispatcher.getRenderer(entity) ?: return@forEach
						// todo BERs filtered to just ours for now until i revamp all this rendering code to render all BERs properly
						if (renderer is BreadModBER)
							renderer.render(
								entity,
								event.partialTick.gameTimeDeltaTicks,
								poseStack,
								bufferSource,
								this.getLight(level, offset),
								NO_OVERLAY
							)
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

	fun getLight(level: Level, pos: Vec3): Int =
		LevelRenderer.getLightColor(level, BlockPos.containing(pos))

	fun shouldRender(cameraPos: Vec3, originPos: Vec3): Boolean =
		Vec3.atCenterOf(originPos.toVec3i()).closerThan(cameraPos, this.getViewDistance())

	fun getViewDistance(): Double = 64.0
}