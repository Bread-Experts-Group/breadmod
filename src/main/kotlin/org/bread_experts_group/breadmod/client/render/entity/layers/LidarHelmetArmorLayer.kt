package org.bread_experts_group.breadmod.client.render.entity.layers

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import org.bread_experts_group.breadmod.client.model.LidarHelmetModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.lidar.LidarHelmetItem
import org.bread_experts_group.breadmod.util.Color

class LidarHelmetArmorLayer(
	renderer: RenderLayerParent<LivingEntity, EntityModel<LivingEntity>>
) : RenderLayer<LivingEntity, EntityModel<LivingEntity>>(renderer) {
	private val lidarHelmetModel: LidarHelmetModel = LidarHelmetModel(localClient.entityModels)
	override fun render(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		livingEntity: LivingEntity,
		limbSwing: Float,
		limbSwingAmount: Float,
		partialTick: Float,
		ageInTicks: Float,
		netHeadYaw: Float,
		headPitch: Float
	) {
		val stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD)
		val item = stack.item
		val entityModel = this.parentModel as? HumanoidModel<*> ?: return

		if (item is LidarHelmetItem) {
			poseStack.pushPose()
			entityModel.head.translateAndRotate(poseStack)
			poseStack.translate(0.0, -0.3, 0.0)
			this.lidarHelmetModel.renderToBuffer(
				poseStack,
				bufferSource.getBuffer(RenderType.entitySolid(LidarHelmetModel.HELMET_TEXTURE)),
				packedLight,
				OverlayTexture.NO_OVERLAY,
				Color.WHITE
			)
			poseStack.popPose()
		}
	}
}