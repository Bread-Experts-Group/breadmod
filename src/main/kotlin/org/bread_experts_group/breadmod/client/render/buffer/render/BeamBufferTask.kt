package org.bread_experts_group.breadmod.client.render.buffer.render

import net.minecraft.client.renderer.RenderType
import net.minecraft.client.player.LocalPlayer
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.render.drawTexturedQuad
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.joml.Vector3f
import org.joml.Vector4f

object BeamBufferTask {
	/**
	 * Draws a line from between [start] and [end],translated according to the current [LocalPlayer]'s position.
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	fun create(start: Vector3f, end: Vector3f, thickness: Float?) {
		val level = localClient.level
		val player = localClient.player
		val bufferSource = localClient.renderBuffers().bufferSource()

		RenderBuffer.add(
			Stage.AFTER_PARTICLES,
			{ event, passthrough ->
				val currentOpacity = passthrough[0] as Float
				val poseStack = event.poseStack
				val camera = event.camera
				val partialTick = event.partialTick.realtimeDeltaTicks

				if (level != null && currentOpacity > 0 && player != null) {
					poseStack.pushPose()
					poseStack.initialTranslate(camera)
					poseStack.translate(0.0, -1.0, 0.0)

					if (thickness != null) {
						// South
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(start.x + 1f, start.y, start.z + 1f),
							Vector3f(start.x - 1f, start.y, start.z + 1f),
							Vector3f(end.x - 1f, end.y, end.z + 1f),
							Vector3f(end.x + 1f, end.y, end.z + 1f)
						)
//            poseStack.translate(2f, 0f, 0f)
						// East
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(start.x + 1f, start.y, start.z - 1f),
							Vector3f(start.x + 1f, start.y, start.z + 1f),
							Vector3f(end.x + 1f, end.y, end.z + 1f),
							Vector3f(end.x + 1f, end.y, end.z - 1f)
						)
						// West
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(start.x - 1f, start.y, start.z + 1f),
							Vector3f(start.x - 1f, start.y, start.z - 1f),
							Vector3f(end.x - 1f, end.y, end.z - 1f),
							Vector3f(end.x - 1f, end.y, end.z + 1f)
						)
						// North
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(start.x - 1f, start.y, start.z - 1f),
							Vector3f(start.x + 1f, start.y, start.z - 1f),
							Vector3f(end.x + 1f, end.y, end.z - 1f),
							Vector3f(end.x - 1f, end.y, end.z - 1f)
						)
						// Start
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(start.x - 1f, start.y, start.z - 1f),
							Vector3f(start.x - 1f, start.y, start.z + 1f),
							Vector3f(start.x + 1f, start.y, start.z + 1f),
							Vector3f(start.x + 1f, start.y, start.z - 1f)
						)
						// End
						drawTexturedQuad(
							BreadMod.modLocation("block", "bread_block"),
							RenderType.translucent(),
							poseStack,
							bufferSource,
							Vector4f(1f, 1f, 1f, currentOpacity),
							Vector3f(end.x - 1f, end.y, end.z - 1f),
							Vector3f(end.x + 1f, end.y, end.z - 1f),
							Vector3f(end.x + 1f, end.y, end.z + 1f),
							Vector3f(end.x - 1f, end.y, end.z + 1f)
						)
					}

					poseStack.popPose()
					passthrough[0] = currentOpacity - 0.1f * partialTick
					false
				} else true
			},
			mutableListOf(1F)
		)
	}
}