package org.bread_experts_group.breadmod.client.model

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat

// todo collection of hat, player, armor, and item held models to be rendered in the mach trail
// todo Armor Rendering: use a combination of HumanoidArmorLayer and HumanoidArmorModel to recreate the
//  model rendering with proper material
// todo Item Rendering: get the item model using ItemRenderer or something similar to render it
//  (issue: can't choose the color the item renders with)
// todo hat and player model rendering is already taken care of in the render function, move those to this class

// todo holy shit figure out how to make this better it's ass
class MachTrailModel(
	playerProfile: GameProfile,
	var currentColor: Int
) {
	private val playerId = playerProfile.id
	private val connection = localClient.connection!!
	private val playerInfo = this.connection.getPlayerInfo(this.playerId)!!
	private val playerSkin = this.playerInfo.skin
	private val playerTexture = this.playerSkin.texture
	private val playerModelType = this.playerSkin.model
	private val player = localClient.level!!.getPlayerByUUID(this.playerId)!!
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
		this.playerModel.renderToBuffer(poseStack, playerModelBuffer, 15728880, NO_OVERLAY, this.currentColor)

		poseStack.translate(0.0, -0.5, 0.0)
		this.chefHatModel.render(poseStack, 15728880, NO_OVERLAY, this.currentColor)
		poseStack.translate(0.0, 0.5, 0.0)
	}
}