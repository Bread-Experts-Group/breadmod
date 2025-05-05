package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object CreativeGeneratorItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	private val originalModel: BakedModel = localClient.modelManager.getModel("block/creative_generator")
	private val starModel: BakedModel = localClient.modelManager.getModel("block/creative_generator_star")

	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val level = localClient.level ?: return
		val partialTick = localClient.timer.gameTimeDeltaTicks
		poseStack.pushPose()
		localClient.itemRenderer.renderItemModel(
			this.originalModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)
		poseStack.translate(0.5, 0.5, 0.5)
		poseStack.mulPose(Axis.YN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
		poseStack.mulPose(Axis.XN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
		poseStack.scaleFlat(0.95f)
		localClient.itemRenderer.renderItemModel(
			this.starModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)
		poseStack.popPose()
	}
}