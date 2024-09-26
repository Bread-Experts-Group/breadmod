package bread.mod.breadmod.client.render.entity

import bread.mod.breadmod.entity.FakePlayer
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation

class FakePlayerRenderer(
    val context: EntityRendererProvider.Context,
) : LivingEntityRenderer<FakePlayer, PlayerModel<FakePlayer>>(
    context,
    PlayerModel<FakePlayer>(
        context.bakeLayer(if (useSlimModel) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER),
        useSlimModel,
    ), 0.5f
) {
    companion object {
        var useSlimModel = false
    }

//    override fun render(
//        entity: MachTrail,
//        entityYaw: Float,
//        partialTick: Float,
//        poseStack: PoseStack,
//        bufferSource: MultiBufferSource,
//        packedLight: Int
//    ) {
//        // need to grab the individual player models instead of render layers
//        val useSlimModel = false
//        val playerModel = PlayerModel<MachTrail>(
//            context.bakeLayer(if (useSlimModel) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER),
//            useSlimModel,
//        ).also {
//            it.young = false
//            it.setupAnim(
//                entity,
//                10f,
//                5f,
//                -1f,
//                10f,
//                10f
//            )
//        }
//        val consumer = bufferSource.getBuffer(RenderType.entitySolid(getTextureLocation(entity)))
//
//        poseStack.scaleFlat(0.9375f)
//        poseStack.translate(0.0, 1.5, 0.0)
//        poseStack.mulPose(Axis.ZN.rotationDegrees(180f))
//        playerModel.renderToBuffer(
//            poseStack,
//            consumer,
//            packedLight,
//            getSkyLightLevel(entity, entity.blockPosition()),
//            Color.WHITE.rgb
//        )
//        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
//    }

    override fun render(
        entity: FakePlayer,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        useSlimModel = isOwnerModelSlim(entity)
//        val level = rgMinecraft.level ?: return
//        try {
//            val owner = level.getEntity(entity.getOwnerID())
//            LogManager.getLogger().info(owner)
//        } catch (e: Exception) {
//            LogManager.getLogger().error(e)
//        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }

    private fun isOwnerModelSlim(entity: FakePlayer): Boolean {
        val model = if (getPlayerInfo(entity) != null) getPlayerInfo(entity)!!.skin.model
        else DefaultPlayerSkin.get(entity.getOwnerUUID()).model
        return model == PlayerSkin.Model.SLIM
    }

    fun getPlayerInfo(entity: FakePlayer): PlayerInfo? {
        val connection = rgMinecraft.connection ?: return null
        return connection.getPlayerInfo(entity.getOwnerUUID())
    }

    override fun getTextureLocation(entity: FakePlayer): ResourceLocation =
        if (getPlayerInfo(entity) != null) getPlayerInfo(entity)!!.skin!!.texture
        else DefaultPlayerSkin.get(entity.getOwnerUUID()).texture
}