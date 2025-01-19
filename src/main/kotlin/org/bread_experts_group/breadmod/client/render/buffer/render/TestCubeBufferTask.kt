package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.math.Axis
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.renderBakedQuads
import org.bread_experts_group.breadmod.client.render.buildTexturedBakedQuad
import org.joml.Vector3f

object TestCubeBufferTask {
	fun create(pos: Vec3) {
		val bufferSource = localClient.renderBuffers().bufferSource()
		val blockPos = BlockPos.containing(pos)
		var rotation = 0f

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, _ ->
				val poseStack = event.poseStack
				val camera = event.camera
				val partialTick = event.partialTick.gameTimeDeltaTicks
				val level = localClient.level ?: return@add true

				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(pos.add(Vec3(0.0, 2.0, 0.0)), camera)
				poseStack.translate(0.5, 0.0, 0.5)
				rotation += 20f * partialTick
				rotation % 360
				poseStack.mulPose(Axis.YN.rotationDegrees(rotation))
				poseStack.translate(-0.5, 0.0, -0.5)
				renderBakedQuads(
					poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
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
					LevelRenderer.getLightColor(level, blockPos),
					OverlayTexture.NO_OVERLAY,
				)
				poseStack.popPose()

				false
			}
		)
	}
}