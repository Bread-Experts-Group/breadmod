package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.math.Axis
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos

object BeamBufferTask {
	fun create(initialPos: Vec3, direction: Vec3, isFirstPerson: Boolean) {
		val level = localClient.level ?: return
		val player = localClient.player ?: return
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer
		val yRot = player.getViewYRot(0f)
		val xRot = player.getViewXRot(0f)

		RenderBuffer.add(
			Stage.AFTER_SOLID_BLOCKS,
			{ event, passthrough ->
				val camera = event.camera
				val poseStack = event.poseStack
				val partialTick = event.partialTick.gameTimeDeltaTicks
				val opacity = passthrough[0] as Float

				if (opacity > 0f) {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(initialPos, camera)
					poseStack.translate(0.0 + 0.5, 1.07 + 0.5, -0.07 + 0.5)
					poseStack.mulPose(Axis.YN.rotationDegrees(yRot + 90f))
					poseStack.mulPose(Axis.ZN.rotationDegrees(xRot))
					poseStack.translate(0.0 - 0.5, -1.07 - 0.5, 0.07 - 0.5)
					poseStack.translate(0.9, 1.52, 0.52)
					poseStack.scale(20f, 0.1f, 0.1f)
					blockRenderer.renderSingleBlock(
						Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(),
						poseStack,
						bufferSource,
						0xFFFFFFF,
						NO_OVERLAY,
						ModelData.EMPTY,
						RenderType.translucent()
					)
					poseStack.popPose()

					passthrough[0] = opacity - 0.1f * partialTick
					false
				} else true
			},
			mutableListOf(1f)
		)
	}
}