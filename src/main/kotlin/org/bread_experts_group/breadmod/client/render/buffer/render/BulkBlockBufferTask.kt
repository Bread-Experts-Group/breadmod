package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.math.Axis
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem.BulkBlockData
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import kotlin.jvm.optionals.getOrNull

object BulkBlockBufferTask {
	fun create(originPos: Vec3, blockData: BulkBlockData) {
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer
		val random = RandomSource.create()
		val modelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val rotation = passthrough[0] as Float
				val poseStack = event.poseStack
				val camera = event.camera
				val level = localClient.level ?: return@add true

				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(originPos, camera, false)

				poseStack.translate(0.5, 0.5, 0.5)
				poseStack.translate(-blockData.aabbCenter)
				poseStack.mulPose(Axis.YN.rotationDegrees(rotation))
				poseStack.translate(blockData.aabbCenter)
				poseStack.translate(-0.5, -0.5, -0.5)

				blockData.blocks.forEach { (offset, pair) ->
					poseStack.pushPose()
					val model = blockRenderer.getBlockModel(pair.first)
					poseStack.translate(offset)
					val direction =
						pair.first.getOptionalValue(HORIZONTAL_FACING) ?: pair.first.getOptionalValue(FACING)
						?: return@forEach
					val facing = direction.getOrNull()
					if (facing != null && pair.first.renderShape == ENTITYBLOCK_ANIMATED) {
						poseStack.translate(0.5f, 0.5f, 0.5f)
						poseStack.mulPose(Axis.YP.rotationDegrees(-(facing.toYRot())))
						poseStack.translate(-0.5f, -0.5f, -0.5f)
					}
					model.getRenderTypes(pair.first, random, modelData).forEach {
						when (pair.first.renderShape ?: return@add true) {
							INVISIBLE            -> {}
							ENTITYBLOCK_ANIMATED -> {
								blockRenderer.renderSingleBlock(
									pair.first,
									poseStack,
									bufferSource,
									LevelRenderer.getLightColor(level, BlockPos.containing(offset)),
									NO_OVERLAY,
									modelData,
									it
								)
							}
							MODEL                -> {
								blockRenderer.renderBatched(
									pair.first,
									pair.second,
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
				poseStack.popPose()
				passthrough[0] = rotation + 5f * event.partialTick.gameTimeDeltaTicks
				val stack = getStackInPlayerHand(localClient.player)
				!stack.`is`(ModItems.BULK_BLOCK_ITEM)
			},
			mutableListOf(
				0f
			)
		)
	}
}