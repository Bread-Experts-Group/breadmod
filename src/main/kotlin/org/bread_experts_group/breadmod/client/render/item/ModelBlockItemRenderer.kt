package org.bread_experts_group.breadmod.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ModelScreen

class ModelBlockItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blocks = stack.getOrDefault(ModDataComponents.MODEL_DATA, listOf())
		when (displayContext) {
			ItemDisplayContext.NONE -> {}
			ItemDisplayContext.THIRD_PERSON_LEFT_HAND -> {}
			ItemDisplayContext.THIRD_PERSON_RIGHT_HAND -> {}
			ItemDisplayContext.FIRST_PERSON_LEFT_HAND -> {
				poseStack.translate(6f, 0f, -0.75f)
				poseStack.scaleFlat(0.75f)
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(90f))
				ModelScreen.renderBlocks(poseStack, buffer, blocks)
				ModelScreen.renderFloor(poseStack, buffer)
			}
			ItemDisplayContext.FIRST_PERSON_RIGHT_HAND -> {
				poseStack.translate(6f, 0f, -2f)
				poseStack.scaleFlat(0.75f)
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(90f))
				ModelScreen.renderBlocks(poseStack, buffer, blocks)
				ModelScreen.renderFloor(poseStack, buffer)
			}
			ItemDisplayContext.HEAD -> {}
			ItemDisplayContext.GUI -> {
				poseStack.translate(0.75f, 1.15f, 0.25f)
				poseStack.scaleFlat(0.25f)
				poseStack.mulPose(Axis.YP.rotationDegrees(90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(90f))
				ModelScreen.renderBlocks(poseStack, buffer, blocks)
				ModelScreen.renderFloor(poseStack, buffer)
			}
			ItemDisplayContext.GROUND -> {
				poseStack.translate(-1.95f, 0.5f, 2.95f)
				poseStack.scaleFlat(0.95f)
				poseStack.mulPose(Axis.YP.rotationDegrees(90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(90f))
				ModelScreen.renderBlocks(poseStack, buffer, blocks)
				ModelScreen.renderFloor(poseStack, buffer)
			}
			ItemDisplayContext.FIXED -> {
				ModelScreen.renderBlocks(poseStack, buffer, blocks)
				ModelScreen.renderFloor(poseStack, buffer)
			}
		}
	}
}