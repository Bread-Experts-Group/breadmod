package org.bread_experts_group.breadmod.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.client.model.ForkliftModel
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift
import java.awt.Color

class ForkliftRenderer(private val context: Context) : EntityRenderer<Forklift>(context) {
	override fun getTextureLocation(entity: Forklift): ResourceLocation = ForkliftModel.FORKLIFT_TEXTURE

	private val model: ForkliftModel = ForkliftModel(this.context.bakeLayer(ForkliftModel.FORKLIFT_LAYER))

	override fun render(
		forklift: Forklift,
		entityYaw: Float,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int
	) {
		this.model.render(forklift, poseStack, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.rgb)
		super.render(forklift, entityYaw, partialTick, poseStack, bufferSource, packedLight)
	}

	override fun getShadowRadius(entity: Forklift): Float = 1f
}