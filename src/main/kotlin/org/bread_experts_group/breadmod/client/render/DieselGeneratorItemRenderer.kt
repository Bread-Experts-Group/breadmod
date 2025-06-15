package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

// todo add door to rendering
object DieselGeneratorItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	private val originalModel: BakedModel = localClient.getModel("block/diesel_generator/diesel_generator")

	//	private val doorModel: BakedModel = localClient.modelManager.getModel(
//		"block/diesel_generator/diesel_generator_door"
//	)
	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
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
		poseStack.popPose()
	}
}