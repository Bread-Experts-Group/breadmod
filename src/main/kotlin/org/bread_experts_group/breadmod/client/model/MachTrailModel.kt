package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.client.render.localClient

class MachTrailModel(val player: LocalPlayer) {
	var red: Float = 0f
	var green: Float = 0f
	var opacity: Float = 0f
	private val walkPosition: Float = this.player.walkAnimation.position()
	private val bufferSource: MultiBufferSource.BufferSource = localClient.renderBuffers().bufferSource()
	private val renderer = localClient.entityRenderDispatcher.getRenderer(this.player) as PlayerRenderer

	fun render(poseStack: PoseStack, partialTick: Float) {
		RenderSystem.setShaderColor(this.red, this.green, 0.1f, this.opacity)
		val f2 = Mth.rotLerp(partialTick, this.player.yHeadRotO, this.player.yHeadRot) -
				Mth.rotLerp(partialTick, this.player.yBodyRotO, this.player.yBodyRot)
		this.renderer.model.setupAnim(
			this.player,
			this.walkPosition,
			this.player.tickCount + partialTick,
			0f,
			f2,
			Mth.lerp(partialTick, this.player.xRotO, this.player.xRot)
		)
		this.renderer.render(
			this.player,
			0f,
			0f,
			poseStack,
			this.bufferSource,
			FULL_BRIGHT
		)
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
	}
}