package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity
import org.bread_experts_group.breadmod.util.toYRotFixed

class MicrowaveRenderer(
	context: Context
) : BreadModBER<MicrowaveBlockEntity>(
	context
) {
	private companion object {
		val DOOR_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door")
		val PLATE_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate")
		val DOOR_MODEL: BakedModel = localClient.modelManager.getModel(this.DOOR_MODEL_LOC)
		val PLATE_MODEL: BakedModel = localClient.modelManager.getModel(this.PLATE_MODEL_LOC)
	}

	private var plateRots: MutableMap<Int, Float> = mutableMapOf()
	private val itemRenderer = this.context.itemRenderer

	override fun render(
		blockEntity: MicrowaveBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val open = blockEntity.blockState.getValue(BlockStateProperties.OPEN)
		if (this.plateRots[blockEntity.hashCode()] == null) this.plateRots[blockEntity.hashCode()] = 0f
		val rotation = this.plateRots[blockEntity.hashCode()] ?: return

		poseStack.pushPose()
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		poseStack.translate(0.5, 0.5, 0.5)
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		poseStack.translate(-0.5, -0.5, -0.5)

		poseStack.pushPose()
		poseStack.scaleFlat(0.9f)
		poseStack.translate(0.65, 0.0, 0.55)
		if (!open) this.plateRots[blockEntity.hashCode()] = rotation + (0.5f * partialTick)
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation))
		poseStack.translate(-0.595, 0.0, -0.50)
		this.renderModel(blockEntity, Companion.PLATE_MODEL, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()

		poseStack.pushPose()
		val stack = blockEntity.getItem(0)
		poseStack.translate(0.60, 0.155, 0.55)
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation))
		poseStack.mulPose(Axis.XN.rotationDegrees(90f))
		poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
		poseStack.scaleFlat(0.4f)
		this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
		poseStack.popPose()

		poseStack.pushPose()
		if (open) {
			poseStack.translate(0.865, 0.0, 0.21)
			poseStack.mulPose(Axis.YN.rotationDegrees(102f))
			poseStack.translate(-0.865, 0.0, -0.21)
		}
		this.renderModel(blockEntity, Companion.DOOR_MODEL, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()

		poseStack.popPose()
	}
}