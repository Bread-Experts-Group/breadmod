package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ModelHandler.Companion.getModelHandler
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ModelScreen

class ModelBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blocks = blockEntity.getModelHandler().blocks
		val facing = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		poseStack.pushPose()
		poseStack.translate(0.5, 0.5, 0.5)
		poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
		poseStack.translate(-0.5, -0.5, -0.5)
		poseStack.mulPose(Axis.XP.rotationDegrees(90f))
		poseStack.translateDiv16(0f, 16f, 0f)
		poseStack.scaleFlat(0.20f)
		ModelScreen.renderFloor(poseStack, bufferSource)
		poseStack.mulPose(Axis.YP.rotationDegrees(180f))
		poseStack.translate(-5f, 0f, 0f)
		ModelScreen.renderBlocks(poseStack, bufferSource, blocks)
		poseStack.popPose()
	}
}