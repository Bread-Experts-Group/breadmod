package org.bread_experts_group.breadmod.client.render.buffer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Transformation
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.drawBlockAtlasCube
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.textureLocation
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.entities
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3
import kotlin.math.roundToInt

object BeamBufferTask {
	var needsNewTask: Boolean = false
	val beamTexture: ResourceLocation = Blocks.LIGHT_BLUE_STAINED_GLASS.textureLocation()

	fun create(lastPose: PoseStack.Pose) {
		val player = localClient.player ?: return
		val pose4f = lastPose.pose()
		val transform = Transformation(pose4f)
		val playerPos = player.position().add(0.0, player.eyeHeight.toDouble(), 0.0)
		val localPos = transform.translation.toVec3()
		val blockCast = player.rayCast(100.0, blocks())
		val entityCast = player.rayCast(100.0, entities())
		val raycast = entityCast ?: blockCast ?: return

		RenderBuffer.add(
			RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, passthrough ->
				val poseStack = event.poseStack
				val opacity = passthrough[0] as Float
				val partialTick = event.partialTick.gameTimeDeltaTicks
				val color = Color.color(255, 255, 255, (255 * opacity).roundToInt())

				if (opacity > 0f) {
					poseStack.pushPose()

					poseStack.initialTranslate(event.camera)
					poseStack.translate(playerPos)
					poseStack.translate(localPos)
					poseStack.mulPose(transform.leftRotation)
					poseStack.mulPose(transform.rightRotation)

					poseStack.translateDiv16(0.0, 0.2, 0.65)
					poseStack.scale(raycast.hitDistance.toFloat(), 0.05f, 0.05f)
					drawBlockAtlasCube(
						this.beamTexture,
						poseStack,
						renderType = RenderType.translucent(),
						color = color
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