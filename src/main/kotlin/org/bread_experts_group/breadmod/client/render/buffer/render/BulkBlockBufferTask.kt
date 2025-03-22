package org.bread_experts_group.breadmod.client.render.buffer.render

import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.util.subtract

/*
https://github.com/PandaMods-Dev/Pandas-Falling-Trees/blob/Dev/1.21.2/common/src/main/java/me/pandamods/fallingtrees/client/render/TreeRenderer.java
https://github.com/PandaMods-Dev/Pandas-Falling-Trees/blob/Dev/1.21.2/common/src/main/java/me/pandamods/fallingtrees/utils/RenderUtils.java
 */

// todo figure out how to offset the position of the rendered structure after rendering it at it's origin BlockPos
object BulkBlockBufferTask {
	fun create(pos: Vec3, blocks: Map<BlockPos, BlockState>) {
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer
		val random = RandomSource.create()
		val modelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val poseStack = event.poseStack
				val camera = event.camera
				val level = localClient.level ?: return@add true

				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(pos, camera, false)

				blocks.forEach { (blockPos, blockState) ->
					poseStack.pushPose()
					val model = blockRenderer.getBlockModel(blockState)
					val offset = blockPos.subtract(pos)
					poseStack.translate(offset)
					model.getRenderTypes(blockState, random, modelData).forEach {
						blockRenderer.renderSingleBlock(
							blockState,
							poseStack,
							bufferSource,
							LevelRenderer.getLightColor(level, BlockPos.containing(pos)),
							NO_OVERLAY,
							modelData,
							it
						)
					}
					poseStack.popPose()
				}
				poseStack.popPose()
				val item = localClient.player!!.getItemInHand(MAIN_HAND)
				!item.`is`(ModItems.WRENCH)
			}
		)
	}
}