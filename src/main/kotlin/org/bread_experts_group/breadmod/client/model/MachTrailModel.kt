package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.client.render.copy
import org.bread_experts_group.breadmod.client.render.localClient

class MachTrailModel(val player: LocalPlayer) {
	var red: Float = 0f
	var green: Float = 0f
	var opacity: Float = 0f
	private val walkPosition: Float = this.player.walkAnimation.position()
	private val bufferSource: MultiBufferSource.BufferSource = localClient.renderBuffers().bufferSource()
	private val renderer = localClient.entityRenderDispatcher.getRenderer(this.player) as PlayerRenderer
	val clonePlayer: LocalPlayer = this.player.copy()

	fun render(poseStack: PoseStack, partialTick: Float) {
		RenderSystem.setShaderColor(this.red, this.green, 0.1f, this.opacity)
		val f2 = Mth.rotLerp(partialTick, this.clonePlayer.yHeadRotO, this.clonePlayer.yHeadRot) -
				Mth.rotLerp(partialTick, this.clonePlayer.yBodyRotO, this.clonePlayer.yBodyRot)
		this.renderer.model.setupAnim(
			this.clonePlayer,
			this.walkPosition,
			this.player.tickCount + partialTick,
			0f,
			f2,
			Mth.lerp(partialTick, this.clonePlayer.xRotO, this.clonePlayer.xRot)
		)
		this.renderer.render(
			this.clonePlayer,
			0f,
			0f,
			poseStack,
			this.bufferSource,
			FULL_BRIGHT
		)
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
	}
}