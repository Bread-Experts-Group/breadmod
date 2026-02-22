package org.bread_experts_group.breadmod.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.pushPop
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.experimental.lidar.handler.LidarHandler
import org.bread_experts_group.breadmod.registry.item.ModItems
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
		val player = localClient.player ?: return
		poseStack.pushPop { pose ->
			if (displayContext.firstPerson()) pose.translate(0.0, 0.05, 0.0)
			localClient.itemRenderer.renderItemModel(
				this.model,
				stack,
				displayContext,
				pose,
				buffer,
				packedOverlay,
				packedLight
			)

			pose.pushPop { innerPose ->
				innerPose.mulPose(Axis.XP.rotationDegrees(90f))
				innerPose.mulPose(Axis.YP.rotationDegrees(90f))
				innerPose.mulPose(Axis.ZP.rotationDegrees(90f))
				innerPose.mulPose(Axis.YN.rotationDegrees(90f))
				innerPose.translate(0.41, -0.715, -0.63)
				innerPose.scaleFlat(0.0025f)
				if (player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModItems.LIDAR_HELMET)) {
					this.renderText("Dots: ${LidarHandler.dotCounter}", innerPose, buffer)
					innerPose.translate(0.0, 8.0, 0.0)
					this.renderText("Size: ${round(LidarHandler.currentDeviation * 100).toInt()}%", innerPose, buffer)
				} else {
					this.renderText("ERR: Helmet", innerPose, buffer)
					innerPose.translate(0.0, 7.0, 0.0)
					this.renderText("not found.", innerPose, buffer)
				}
			}
		}
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