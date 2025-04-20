package org.bread_experts_group.breadmod.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.entity.actual.Rocket
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

class RocketRenderer(
	private val context: EntityRendererProvider.Context
) : EntityRenderer<Rocket>(context) {
	override fun getTextureLocation(entity: Rocket): ResourceLocation = modLocation()

	override fun render(
		entity: Rocket,
		entityYaw: Float,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int
	) {
		val blocks = entity.entityData.get(Rocket.BLOCKS)
		val origin = entity.entityData.get(Rocket.ORIGIN)
		val center = entity.entityData.get(Rocket.BOTTOM_CENTER)
//		LogManager.getLogger().info(center)
		blocks.forEach { (pos, state) ->
			val offset = pos.offset(-origin)
			poseStack.pushPose()
			poseStack.translate(offset.toVec3())
			poseStack.translate(-0.5, 0.0, -0.5)
			poseStack.translate(center.toVec3())
			this.context.blockRenderDispatcher.renderSingleBlock(
				state,
				poseStack,
				bufferSource,
				packedLight,
				OverlayTexture.NO_OVERLAY,
			)
			poseStack.popPose()
		}
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
	}
}