package org.bread_experts_group.breadmod.client.render.buffer.render

import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buildTexturedBakedQuad
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.renderBakedQuads
import org.bread_experts_group.breadmod.client.render.translate
import org.joml.Vector3f

object MovingBreadBlockBufferTask {
	/**
	 * Draws a moving bread block from [start] and moves in [direction].
	 */
	fun create(start: Vec3, direction: Vec3) {
		val bufferSource = localClient.renderBuffers().bufferSource()

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val currentOpacity = passthrough[0] as Float
				val position = passthrough[1] as Vec3
				val poseStack = event.poseStack
				val camera = event.camera
				val partialTick = event.partialTick.gameTimeDeltaTicks

				if (currentOpacity > 0) {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(start, camera)
					poseStack.translate(position)

					renderBakedQuads(
						poseStack.last(), bufferSource.getBuffer(RenderType.translucent()),
						1f, 1f, 1f,
						listOf(
							buildTexturedBakedQuad( // North
								modLocation("block", "bread_block")
							),
							buildTexturedBakedQuad( // Top
								modLocation("block", "bread_block"),
								topLeft = Vector3f(0f, 0f, 0f),
								topRight = Vector3f(1f, 0f, 0f),
								bottomLeft = Vector3f(0f, 0f, 1f),
								bottomRight = Vector3f(1f, 0f, 1f)
							),
							buildTexturedBakedQuad( // East
								modLocation("block", "bread_block"),
								topLeft = Vector3f(1f, 0f, 1f),
								topRight = Vector3f(1f, 0f, 0f),
								bottomLeft = Vector3f(1f, -1f, 1f),
								bottomRight = Vector3f(1f, -1f, 0f)
							),
							buildTexturedBakedQuad( // West
								modLocation("block", "bread_block"),
								topLeft = Vector3f(0f, 0f, 0f),
								topRight = Vector3f(0f, 0f, 1f),
								bottomLeft = Vector3f(0f, -1f, 0f),
								bottomRight = Vector3f(0f, -1f, 1f)
							),
							buildTexturedBakedQuad( // South
								modLocation("block", "bread_block"),
								topLeft = Vector3f(0f, 0f, 1f),
								topRight = Vector3f(1f, 0f, 1f),
								bottomLeft = Vector3f(0f, -1f, 1f),
								bottomRight = Vector3f(1f, -1f, 1f)
							),
							buildTexturedBakedQuad( // Bottom
								modLocation("block", "bread_block"),
								topLeft = Vector3f(0f, -1f, 1f),
								topRight = Vector3f(1f, -1f, 1f),
								bottomLeft = Vector3f(0f, -1f, 0f),
								bottomRight = Vector3f(1f, -1f, 0f)
							)
						),
						0xFFFFFFF,
						OverlayTexture.NO_OVERLAY,
					)

					poseStack.popPose()
					passthrough[0] = currentOpacity - 0.1f * partialTick
					passthrough[1] = position.add(direction)
					false
				} else true
			},
			mutableListOf(1F, direction)
		)
	}
}