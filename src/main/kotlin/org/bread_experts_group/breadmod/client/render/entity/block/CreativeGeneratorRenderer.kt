package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.CreativeGeneratorBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.machine.CreativeGeneratorBlock

class CreativeGeneratorRenderer(context: Context) : BreadModBER<CreativeGeneratorBlockEntity>(context) {
	private val starModel: BakedModel = this.modelManager.getModel("block/creative_generator_star")

	override fun render(
		blockEntity: CreativeGeneratorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val level = blockEntity.level ?: return
		val enabled = blockEntity.blockState.getValue(CreativeGeneratorBlock.ENABLED)

		poseStack.pushPose()
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		if (enabled) {
			poseStack.translate(0.5, 0.5, 0.5)
			poseStack.mulPose(Axis.YN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
			poseStack.mulPose(Axis.XN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
			poseStack.scaleFlat(0.95f)
			localClient.blockRenderer.modelRenderer.renderModel(
				poseStack.last(),
				bufferSource.getBuffer(RenderType.solid()),
				blockEntity.blockState,
				this.starModel,
				1f,
				1f,
				1f,
				LightTexture.FULL_BRIGHT,
				packedOverlay,
				ModelData.EMPTY,
				RenderType.solid()
			)
		}
		poseStack.popPose()
	}
}