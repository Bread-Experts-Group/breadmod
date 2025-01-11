package org.bread_experts_group.breadmod.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.client.render.localClient

class FakePlayerRenderer(
	context: EntityRendererProvider.Context,
) : LivingEntityRenderer<FakePlayer, PlayerModel<FakePlayer>>(
	context,
	PlayerModel<FakePlayer>(
		context.bakeLayer(if (this.useSlimModel) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER),
		this.useSlimModel,
	), 0.5f
) {
	companion object {
		private var useSlimModel: Boolean = true
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
//        model.body.translateAndRotate(poseStack)
//        val level = rgMinecraft.level ?: return
//        try {
//            val owner = level.getEntity(entity.getOwnerID())
//            LogManager.getLogger().info(owner)
//        } catch (e: Exception) {
//            LogManager.getLogger().error(e)
//        }
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
	}

	private fun getPlayerInfo(entity: FakePlayer): PlayerInfo? {
		val connection = localClient.connection ?: return null
		return connection.getPlayerInfo(entity.getOwnerUUID())
	}

	override fun getTextureLocation(entity: FakePlayer): ResourceLocation =
		if (this.getPlayerInfo(entity) != null) this.getPlayerInfo(entity)!!.skin.texture
		else DefaultPlayerSkin.get(entity.getOwnerUUID()).texture
}