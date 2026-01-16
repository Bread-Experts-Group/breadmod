package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translateToSide
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3

@ToolGunMode
@Suppress("unused")
class LidarMode : IToolGunMode {
	override fun action(
		level: Level,
		player: Player,
		stack: ItemStack,
		usedHand: InteractionHand,
		data: ToolGunData
	) {
		if (!level.isClientSide) return
		player.rayCast(50.0, blocks())?.let { hit ->
			val colors = arrayOf(Color.GREEN, Color.YELLOW, Color.RED)
			RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_LEVEL, { event, _ ->
				val poseStack = event.poseStack
				val distance = player.position().distanceTo(hit.hitPosition)
				val color = if (distance < 5) colors[2] else if (distance < 15) colors[1] else colors[0]
				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(hit.blockPosition.toVec3(), event.camera, false)
				poseStack.translateToSide(hit.hitSide)
				drawQuad(poseStack, renderType = ModRenderType.LIDAR, color = color)
				poseStack.popPose()
				false
			})
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("lidar_mode")
}