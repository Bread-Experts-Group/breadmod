package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.registry.block.actual.entity.DieselGeneratorBlockEntity
import org.bread_experts_group.breadmod.util.toYRotFixed

// todo the rest of the models
class DieselGeneratorRenderer(context: Context) : BreadModBER<DieselGeneratorBlockEntity>(context) {
	private val doorModel: BakedModel = this.modelManager.getModel("block/diesel_generator/diesel_generator_door")

	override fun render(
		blockEntity: DieselGeneratorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		poseStack.pushPose()
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		this.renderModel(blockEntity, this.doorModel, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
	}
}