package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.toYRotFixed

class MicrowaveRenderer(
	context: BlockEntityRendererProvider.Context
) : BreadModBER(context) {
	private companion object {
		val modelManager: ModelManager = localClient.modelManager
		val DOOR_MODEL: BakedModel =
			this.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door")
		val PLATE_MODEL: BakedModel =
			this.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate")
	}

	private var plateRots: MutableMap<Int, Float> = mutableMapOf()
	private val itemRenderer: ItemRenderer = this.context.itemRenderer

	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(HORIZONTAL_FACING)
		val open = blockEntity.blockState.getValue(OPEN)
		if (this.plateRots[blockEntity.hashCode()] == null) this.plateRots[blockEntity.hashCode()] = 0f
		val rotation = this.plateRots[blockEntity.hashCode()] ?: return
		val storage = blockEntity.getCapability(Capabilities.ItemHandler.BLOCK)

		poseStack.pushPose()
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
		val stack = storage.getStackInSlot(0)
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