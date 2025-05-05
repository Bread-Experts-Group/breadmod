package org.bread_experts_group.breadmod.client.render.buffer

import com.mojang.math.Axis
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos

object BeamBufferTask {
	var xOffset: Double = 0.0
	var yOffset: Double = 0.0
	var zOffset: Double = 0.0
	var rotationEnabled: Boolean = false
	var usePlayerRot: Boolean = false
	fun create(initialPos: Vec3, yRot: Float, xRot: Float) {
		val player = localClient.player ?: return
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockRenderer = localClient.blockRenderer
		val axisModel = localClient.modelManager.getModel("block/axis")

		RenderBuffer.add(
			Stage.AFTER_SOLID_BLOCKS,
			{ event, passthrough ->
				val camera = event.camera
				val poseStack = event.poseStack
				val partialTick = event.partialTick.gameTimeDeltaTicks
				val opacity = passthrough[0] as Float
				val pyRot = player.getViewYRot(partialTick)
				val pxRot = player.getViewXRot(partialTick)

				if (opacity > 0f) {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(initialPos, camera, false)
					poseStack.translate(this.xOffset, this.yOffset, this.zOffset)
					if (this.rotationEnabled) {
						if (!this.usePlayerRot) {
							poseStack.mulPose(Axis.YN.rotationDegrees(yRot + 90f))
							poseStack.mulPose(Axis.ZN.rotationDegrees(xRot))
						} else {
							poseStack.mulPose(Axis.YN.rotationDegrees(pyRot + 90f))
							poseStack.mulPose(Axis.ZN.rotationDegrees(pxRot))
						}
					}
					poseStack.translate(-this.xOffset, -this.yOffset, -this.zOffset)
					poseStack.translate(this.xOffset, this.yOffset, this.zOffset)
					blockRenderer.modelRenderer.renderModel(
						poseStack.last(),
						bufferSource.getBuffer(RenderType.translucent()),
						Blocks.AIR.defaultBlockState(),
						axisModel,
						1f,
						1f,
						1f,
						15728880,
						NO_OVERLAY,
						ModelData.EMPTY,
						RenderType.translucent()
					)
					poseStack.scale(20f, 0.1f, 0.1f)
					poseStack.translate(0.0, -0.5, -0.5)
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