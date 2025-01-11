package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.math.Axis
import net.minecraft.Util
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
import org.bread_experts_group.breadmod.client.render.texturedBakedQuadTest
import org.joml.Vector3f

object TestCubeBufferTask {
	fun create(pos: Vec3) {
		val bufferSource = localClient.renderBuffers().bufferSource()

		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, _ ->
				val poseStack = event.poseStack
				val camera = event.camera
//				val partialTick = event.partialTick.gameTimeDeltaTicks
				val level = localClient.level ?: return@add true
				val millis = Util.getMillis()

				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(pos.add(Vec3(0.0, 2.0, 0.0)), camera)
				poseStack.translate(0.5, 0.0, 0.5)
				poseStack.mulPose(Axis.YN.rotationDegrees((millis.toFloat() / 20 % 360)))
				poseStack.translate(-0.5, 0.0, -0.5)
				renderBakedQuads(
					poseStack.last(), bufferSource.getBuffer(RenderType.solid()),
					1f, 1f, 1f,
					listOf(
						texturedBakedQuadTest(modLocation("block", "bread_block")),
						texturedBakedQuadTest(
							modLocation("block", "bread_block"),
							topLeft = Vector3f(0f, 0f, 0f),
							topRight = Vector3f(1f, 0f, 0f),
							bottomLeft = Vector3f(0f, 0f, 1f),
							bottomRight = Vector3f(1f, 0f, 1f)
						)
					),
					LevelRenderer.getLightColor(level, BlockPos.containing(pos)),
					OverlayTexture.NO_OVERLAY,
				)
				poseStack.popPose()

				false
			}
		)
	}
}