package bread.mod.breadmod.client.model

import bread.mod.breadmod.util.render.rgMinecraft
import bread.mod.breadmod.util.render.scaleFlat
import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.player.Player

// todo collection of hat, player, armor, and item held models to be rendered in the mach trail
// todo Armor Rendering: use a combination of HumanoidArmorLayer and HumanoidArmorModel to recreate the model rendering with proper material
// todo Item Rendering: get the item model using ItemRenderer or something similar to render it (issue: can't choose the color the item renders with)
// todo hat and player model rendering is already taken care of in the render function, move those to this class

class MachTrailModel(
    playerProfile: GameProfile,
    var currentColor: Int
) {
    private val playerId = playerProfile.id
    private val connection = rgMinecraft.connection!!
    private val playerInfo = connection.getPlayerInfo(playerId)!!
    private val playerSkin = playerInfo.skin
    private val playerTexture = playerSkin.texture
    private val playerModelType = playerSkin.model
    private val player = rgMinecraft.level!!.getPlayerByUUID(playerId)!!
    private val limbSwing = player.walkAnimation.position()

    private val entityModels = rgMinecraft.entityModels
    private val bufferSource = rgMinecraft.renderBuffers().bufferSource()

    private val chefHatModel = ChefHatModel(entityModels)
    private val playerModel = PlayerModel<Player>(
        entityModels.bakeLayer(
            if (playerModelType == PlayerSkin.Model.SLIM) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER
        ),
        playerModelType == PlayerSkin.Model.SLIM
    )
//    private val outerArmorModel = HumanoidArmorModel<Player>(
//        entityModels.bakeLayer(
//            if (playerModelType == PlayerSkin.Model.SLIM) ModelLayers.PLAYER_SLIM_OUTER_ARMOR else ModelLayers.PLAYER_OUTER_ARMOR
//        )
//    )

    init {
        playerModel.young = false
    }

    fun render(poseStack: PoseStack) {
        poseStack.scaleFlat(0.9375f)
        playerModel.setupAnim(
            player,
            limbSwing,
            0.6f,
            -1f, 0f, 0f
        )

        val playerModelBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(playerTexture))
        playerModel.renderToBuffer(poseStack, playerModelBuffer, 15728880, NO_OVERLAY, currentColor)

        poseStack.translate(0.0, -0.5, 0.0)
        chefHatModel.render(poseStack, 15728880, NO_OVERLAY, currentColor)
        poseStack.translate(0.0, 0.5, 0.0)
    }
}