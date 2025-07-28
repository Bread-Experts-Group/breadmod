package org.bread_experts_group.breadmod.client.render.entity.block
//class MicrowaveRenderer(
//	context: Context
//) : BreadModBER<MicrowaveBlockEntity>(
//	context
//) {
//	private companion object {
//		val modelManager: ModelManager = localClient.modelManager
//		val DOOR_MODEL: BakedModel =
//			this.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door")
//		val PLATE_MODEL: BakedModel =
//			this.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate")
//	}
//
//	private var plateRots: MutableMap<Int, Float> = mutableMapOf()
//	private val itemRenderer: ItemRenderer = this.context.itemRenderer
//
//	override fun renderBM(
//		blockEntity: MicrowaveBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
//		val open = blockEntity.blockState.getValue(BlockStateProperties.OPEN)
//		if (this.plateRots[blockEntity.hashCode()] == null) this.plateRots[blockEntity.hashCode()] = 0f
//		val rotation = this.plateRots[blockEntity.hashCode()] ?: return
//
//		poseStack.pushPose()
//		poseStack.translate(0.5, 0.5, 0.5)
//		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
//		poseStack.translate(-0.5, -0.5, -0.5)
//
//		poseStack.pushPose()
//		poseStack.scaleFlat(0.9f)
//		poseStack.translate(0.65, 0.0, 0.55)
//		if (!open) this.plateRots[blockEntity.hashCode()] = rotation + (0.5f * partialTick)
//		poseStack.mulPose(Axis.YP.rotationDegrees(rotation))
//		poseStack.translate(-0.595, 0.0, -0.50)
//		this.renderModel(blockEntity, Companion.PLATE_MODEL, poseStack, bufferSource, packedOverlay)
//		poseStack.popPose()
//
//		poseStack.pushPose()
//		val stack = blockEntity.getItem(0)
//		poseStack.translate(0.60, 0.155, 0.55)
//		poseStack.mulPose(Axis.YP.rotationDegrees(rotation))
//		poseStack.mulPose(Axis.XN.rotationDegrees(90f))
//		poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
//		poseStack.scaleFlat(0.4f)
//		this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
//		poseStack.popPose()
//
//		poseStack.pushPose()
//		if (open) {
//			poseStack.translate(0.865, 0.0, 0.21)
//			poseStack.mulPose(Axis.YN.rotationDegrees(102f))
//			poseStack.translate(-0.865, 0.0, -0.21)
//		}
//		this.renderModel(blockEntity, Companion.DOOR_MODEL, poseStack, bufferSource, packedOverlay)
//		poseStack.popPose()
//
//		poseStack.popPose()
//	}
//}