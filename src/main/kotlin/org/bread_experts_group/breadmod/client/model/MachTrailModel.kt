package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat

// todo completely rewrite with better model getting
class MachTrailModel(
	val player: Player,
	private val playerInfo: PlayerInfo,
	var currentColor: Int
) {
	private val playerSkin = this.playerInfo.skin
	private val playerTexture = this.playerSkin.texture
	private val playerModelType = this.playerSkin.model
	private val limbSwing = this.player.walkAnimation.position()
	private val entityModels = localClient.entityModels
	private val bufferSource = localClient.renderBuffers().bufferSource()
	private val chefHatModel = ChefHatModel(this.entityModels)
	private val playerModel = PlayerModel<Player>(
		this.entityModels.bakeLayer(
			if (this.playerModelType == PlayerSkin.Model.SLIM) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER
		),
		this.playerModelType == PlayerSkin.Model.SLIM
	)

	init {
		this.playerModel.young = false
	}

	fun render(poseStack: PoseStack) {
		poseStack.scaleFlat(0.9375f)
		this.playerModel.setupAnim(
			this.player,
			this.limbSwing,
			0.6f,
			-1f, 0f, 0f
		)
		val playerModelBuffer = this.bufferSource.getBuffer(RenderType.entityTranslucent(this.playerTexture))
		this.playerModel.renderToBuffer(poseStack, playerModelBuffer, FULL_BRIGHT, NO_OVERLAY, this.currentColor)

		poseStack.translate(0.0, -0.5, 0.0)
		this.chefHatModel.render(poseStack, FULL_BRIGHT, NO_OVERLAY, this.currentColor)
		poseStack.translate(0.0, 0.5, 0.0)
	}
}