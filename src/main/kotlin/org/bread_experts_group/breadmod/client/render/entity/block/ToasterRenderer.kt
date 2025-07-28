package org.bread_experts_group.breadmod.client.render.entity.block
//class ToasterRenderer(
//	context: Context
//) : BreadModBER<ToasterBlockEntity>(
//	context
//) {
//	private companion object {
//		val HANDLE_MODEL: BakedModel = localClient.getModel("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
//	}
//
//	private val itemRenderer: ItemRenderer = this.context.itemRenderer
//
//	override fun renderBM(
//		blockEntity: ToasterBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
//		val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)
//
//		poseStack.pushPose()
//		when (blockRotation) {
//			SOUTH -> {
//				poseStack.mulPose(Axis.YP.rotationDegrees(180f))
//				poseStack.translate(-1.0, 0.0, -1.0)
//			}
//			WEST  -> {
//				poseStack.mulPose(Axis.YP.rotationDegrees(90f))
//				poseStack.translate(-1.0, 0.0, 0.0)
//			}
//			EAST  -> {
//				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
//				poseStack.translate(0.0, 0.0, -1.0)
//			}
//			else  -> {}
//		}
//		poseStack.translate(0.0, if (triggered) -0.13 else 0.0, 0.0)
//		this.renderModel(blockEntity, Companion.HANDLE_MODEL, poseStack, bufferSource, packedOverlay)
//		poseStack.popPose()
//		val stack = blockEntity.getItem(0)
//
//		poseStack.pushPose()
//		poseStack.translate(0.5, 0.3, 0.61)
//		poseStack.scaleFlat(0.6f)
//		if (blockRotation == SOUTH || blockRotation == NORTH) {
//			poseStack.mulPose(Axis.YN.rotationDegrees(90f))
//			poseStack.translateDiv16(-2.8, 0.0, 3.0)
//		}
//		if (!triggered) {
//			if (stack.count == 2) {
//				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
//				poseStack.translate(0.0, 0.0, -0.37)
//				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
//			} else this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
//		}
//		poseStack.popPose()
//	}
//
//	override fun getViewDistance(): Int = 32
//}