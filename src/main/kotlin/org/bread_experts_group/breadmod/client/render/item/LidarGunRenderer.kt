package org.bread_experts_group.breadmod.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarHandler
import org.bread_experts_group.breadmod.util.Color
import kotlin.math.round

class LidarGunRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	private val model: BakedModel = localClient.getModel("item/lidar_gun_item")

	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		if (displayContext.firstPerson()) poseStack.translate(0.0, 0.05, 0.0)
		localClient.itemRenderer.renderItemModel(
			this.model,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)

		poseStack.pushPose()
		poseStack.mulPose(Axis.XP.rotationDegrees(90f))
		poseStack.mulPose(Axis.YP.rotationDegrees(90f))
		poseStack.mulPose(Axis.ZP.rotationDegrees(90f))
		poseStack.mulPose(Axis.YN.rotationDegrees(90f))
		poseStack.translate(0.41, -0.715, -0.63)
		poseStack.scaleFlat(0.0025f)
		this.renderText("Dots: ${LidarHandler.dotCounter}", poseStack, buffer)
		poseStack.translate(0.0, 8.0, 0.0)
		this.renderText("Size: ${round(LidarHandler.currentDeviation * 100).toInt()}%", poseStack, buffer)
		poseStack.popPose()
		poseStack.popPose()
	}

	private fun renderText(text: String, poseStack: PoseStack, bufferSource: MultiBufferSource) {
		localClient.font.renderText(
			Component.literal(text).visualOrderText,
			Color.RED,
			Color.NONE,
			poseStack,
			bufferSource,
			false,
			LightTexture.FULL_BRIGHT
		)
	}
}