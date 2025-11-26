package org.bread_experts_group.breadmod.experimental.camera_viewer.camera

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker

class CameraBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val handler = blockEntity.getLerpTicker<Int>()
		poseStack.pushPose()
		poseStack.translate(0.5, 0.5, 0.5)
		poseStack.mulPose(Axis.YN.rotationDegrees(handler.getLerpedValue(0, partialTick)))
		localClient.blockRenderer.modelRenderer.renderModel(
			poseStack.last(),
			bufferSource.getBuffer(RenderType.solid()),
			blockEntity.blockState,
			localClient.getModel("block/axis"),
			1f,
			1f,
			1f,
			packedLight,
			packedOverlay,
			ModelData.EMPTY,
			RenderType.solid()
		)
		poseStack.popPose()

		poseStack.pushPose()
		poseStack.drawTextOnBlockSide(
			this.context.font,
			Component.literal("${handler.getLerpedValue(0, partialTick).toInt()}"),
			0.0,
			0.0,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.05f
		)
		poseStack.popPose()
	}
}