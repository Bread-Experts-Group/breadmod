package org.bread_experts_group.breadmod.client.render.entity.layers

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.FoxModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.FastColor
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.component.DyedItemColor
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.model.ChefHatModel.Companion.HAT_TEXTURE
import org.bread_experts_group.breadmod.registry.item.actual.armor.ChefHatItem
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

/**
 * Armor layer for the Chef Hat model.
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see ChefHatModel
 * @see ChefHatItem
 */
class ChefHatArmorLayer(
	renderer : RenderLayerParent<LivingEntity, EntityModel<LivingEntity>>
) : RenderLayer<LivingEntity, EntityModel<LivingEntity>>(renderer) {
	private val chefHatModel = ChefHatModel(localClient.entityModels)
	override fun render(
		poseStack : PoseStack,
		bufferSource : MultiBufferSource,
		packedLight : Int,
		livingEntity : LivingEntity,
		limbSwing : Float,
		limbSwingAmount : Float,
		partialTick : Float,
		ageInTicks : Float,
		netHeadYaw : Float,
		headPitch : Float
	) {
		val stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD)
		val item = stack.item
		val entityModel = this.parentModel

		if (item is ChefHatItem) {
			val color = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(stack, Color.WHITE.rgb))

			poseStack.pushPose()
			when (entityModel) {
				is HumanoidModel<*> -> {
					// todo figure out why it doesn't translate with zombie heads
					entityModel.head.translateAndRotate(poseStack)
					poseStack.translate(0.0, -0.5, 0.0)
				}
				is FoxModel<*>      -> {
					entityModel.head.translateAndRotate(poseStack)
					poseStack.translate(0.06, -0.1, -0.13)
					poseStack.scaleFlat(0.9f)
				}
			}

			this.renderHat(poseStack, bufferSource, packedLight, color)
			poseStack.popPose()
		}
	}

	private fun renderHat(
		poseStack : PoseStack,
		buffer : MultiBufferSource,
		packedLight : Int,
		color : Int
	) {
		poseStack.scaleFlat(1.05f)
		this.chefHatModel.renderToBuffer(
			poseStack,
			buffer.getBuffer(RenderType.entitySolid(HAT_TEXTURE)),
			packedLight,
			OverlayTexture.NO_OVERLAY,
			color
		)
	}
}