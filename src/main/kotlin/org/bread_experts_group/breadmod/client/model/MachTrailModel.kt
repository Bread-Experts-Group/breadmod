package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat

// todo look into rewriting with gathering all the models on the player entity and actually setting their anim pose properly.
class MachTrailModel(
	val player: Player,
	private val playerInfo: PlayerInfo,
	var currentColor: Int
) {
	private val playerSkin: PlayerSkin = this.playerInfo.skin
	private val playerTexture: ResourceLocation = this.playerSkin.texture
	private val playerModelType: PlayerSkin.Model = this.playerSkin.model
	private val limbSwing: Float = this.player.walkAnimation.position()
	private val entityModels: EntityModelSet = localClient.entityModels
	private val bufferSource: MultiBufferSource.BufferSource = localClient.renderBuffers().bufferSource()
	private val chefHatModel: ChefHatModel = ChefHatModel(this.entityModels)
	private val playerModel: PlayerModel<Player> = PlayerModel<Player>(
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