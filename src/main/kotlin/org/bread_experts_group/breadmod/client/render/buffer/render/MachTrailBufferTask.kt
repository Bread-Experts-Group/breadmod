package org.bread_experts_group.breadmod.client.render.buffer.render

import com.mojang.math.Axis
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import org.bread_experts_group.breadmod.client.model.MachTrailModel
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.client.MachTrailData
import java.awt.Color

// todo revamp with better translation and rotation logic
object MachTrailBufferTask {
	/**
	 * A map holding mach trail data for each player currently running with the chef hat.
	 */
	val machTrailMap: MutableMap<Player, MachTrailData> = mutableMapOf()

	/**
	 * Renders a single instance of the mach trail behind the player.
	 *
	 * @author Logan McLean
	 * @see MachTrailData
	 * @see org.bread_experts_group.breadmod.registry.item.actual.armor.ChefHatItem
	 */
	// todo head rotations
	fun create(player: Player) {
		val x = player.x
		val y = player.y
		val z = player.z
		val yRot = -player.rotationVector.y
		val connection = localClient.connection ?: return
		val info = connection.getPlayerInfo(player.uuid) ?: return
		val machTrailModel = MachTrailModel(player, info, 0)

		RenderBuffer.add(
			Stage.AFTER_PARTICLES,
			{ event, passthrough ->
				val currentOpacity = passthrough[0] as Float
				val redValue = passthrough[1] as Float
				val greenValue = passthrough[2] as Float
				machTrailModel.currentColor = Color(
					redValue,
					greenValue,
					0.1f,
					Math.clamp(currentOpacity, 0f, 1f)
				).rgb
				val poseStack = event.poseStack
				val camera = event.camera
				val partialTick = event.partialTick.realtimeDeltaTicks

				if (currentOpacity > 0) {
					poseStack.pushPose()
					poseStack.mulPose(Axis.YN.rotationDegrees(-yRot))
					poseStack.translate(0.0, 0.0, -0.3)
					poseStack.mulPose(Axis.YN.rotationDegrees(yRot))
					poseStack.initialTranslate(camera)
					poseStack.translate(x, y, z)
					poseStack.translate(0.0, 1.4, 0.0)
					poseStack.mulPose(Axis.XN.rotationDegrees(180f))
					poseStack.mulPose(Axis.YN.rotationDegrees(yRot))

					machTrailModel.render(poseStack)

					poseStack.popPose()

					passthrough[1] = Math.clamp(redValue + 0.05f, 0f, 1f)
					passthrough[2] = Math.clamp(greenValue - 0.05f, 0f, 1f)
					passthrough[0] = currentOpacity - 0.1f * partialTick
					false
				} else true
			},
			mutableListOf(
				0.8F,
				0F,
				1F
			)
		)
	}
}