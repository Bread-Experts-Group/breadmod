package org.bread_experts_group.breadmod.client.render.entity.layers

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.model.GluonGunBackpackModel
import org.bread_experts_group.breadmod.registry.item.actual.armor.GluonGunBackpackItem
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import org.joml.Matrix4f
import java.awt.Color

class GluonGunBackpackArmorLayer(
	renderer : RenderLayerParent<LivingEntity, EntityModel<LivingEntity>>
) : RenderLayer<LivingEntity, EntityModel<LivingEntity>>(renderer) {
	private val backpackModel = GluonGunBackpackModel(localClient.entityModels)
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
		val stack = livingEntity.getItemBySlot(EquipmentSlot.CHEST)
		val item = stack.item
		val entityModel = this.parentModel
		if (item is GluonGunBackpackItem) {
			poseStack.pushPose()
			when (entityModel) {
				is HumanoidModel<*> -> {
					entityModel.body.translateAndRotate(poseStack)
				}
			}
			this.renderLeash(livingEntity, partialTick, poseStack, bufferSource)

			poseStack.translate(0.0, -0.1, 0.12)
			poseStack.mulPose(Axis.YN.rotationDegrees(180f))
			poseStack.scaleFlat(0.5f)
			this.backpackModel.render(poseStack, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.rgb)
			poseStack.popPose()
		}
	}

	private fun renderLeash(
		livingEntity : LivingEntity,
		partialTick : Float,
		poseStack : PoseStack,
		bufferSource : MultiBufferSource
	) {
		poseStack.pushPose()
		val vec3 = livingEntity.getPosition(partialTick) + Vec3(-1.0, 0.5, 0.0) // The end of the rope
		poseStack.translate(-0.1, 0.1, 0.5) // the start of the rope
		val startX = (vec3.x - Mth.lerp(partialTick.toDouble(), livingEntity.xo, livingEntity.x)).toFloat()
		val startY = (vec3.y - Mth.lerp(partialTick.toDouble(), livingEntity.yo, livingEntity.y)).toFloat()
		val startZ = (vec3.z - Mth.lerp(partialTick.toDouble(), livingEntity.zo, livingEntity.z)).toFloat()
		val vertexConsumer = bufferSource.getBuffer(RenderType.leash())
		val matrix4f = poseStack.last().pose()
		val f4 = Mth.invSqrt(startX * startX + startZ * startZ) * 0.025f / 2.0f
		val f5 = startZ * f4 + 0.05f
		val f6 = startX * f4 + 0.05f

		for (i1 in 0 .. 24) {
			this.addVertexPair(
				vertexConsumer,
				matrix4f,
				startX,
				startY,
				startZ,
				14,
				14,
				14,
				14,
				0.025f,
				f5,
				f6,
				i1,
				false
			)
		}

		for (j1 in 24 downTo 0) {
			this.addVertexPair(
				vertexConsumer,
				matrix4f,
				startX,
				startY,
				startZ,
				14,
				14,
				14,
				14,
				0.0f,
				f5,
				f6,
				j1,
				true
			)
		}

		poseStack.popPose()
	}

	fun addVertexPair(
		buffer : VertexConsumer,
		pose : Matrix4f,
		startX : Float,
		startY : Float,
		startZ : Float,
		entityBlockLight : Int,
		holderBlockLight : Int,
		entitySkyLight : Int,
		holderSkyLight : Int,
		dy : Float,
		dx : Float,
		dz : Float,
		index : Int,
		reverse : Boolean
	) {
		val f = index.toFloat() / 24.0f
		val i = Mth.lerp(f, entityBlockLight.toFloat(), holderBlockLight.toFloat()).toInt()
		val j = Mth.lerp(f, entitySkyLight.toFloat(), holderSkyLight.toFloat()).toInt()
		val k = LightTexture.pack(i, j)
		val f1 = if (index % 2 == (if (reverse) 1 else 0)) 0.7f else 1.0f
		val f2 = 0.5f * f1
		val f3 = 0.4f * f1
		val f4 = 0.3f * f1
		val f5 = startX * f
		val f6 = if (startY > 0.0f) startY * f * f else startY - startY * (1.0f - f) * (1.0f - f)
		val f7 = startZ * f
		buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(f2, f3, f4, 1.0f).setLight(k)
		buffer.addVertex(pose, f5 + dx, f6 + 0.025f - dy, f7 - dz).setColor(f2, f3, f4, 1.0f).setLight(k)
	}
}