package org.bread_experts_group.breadmod.client.render.entity.block
//class CreativeGeneratorRenderer(context: Context) : BreadModBER<CreativeGeneratorBlockEntity>(context) {
//	private val starModel: BakedModel = localClient.getModel("block/creative_generator_star")
//
//	override fun renderBM(
//		blockEntity: CreativeGeneratorBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val level = blockEntity.level ?: return
//		val enabled = blockEntity.blockState.getValue(CreativeGeneratorBlock.ENABLED)
//
//		poseStack.pushPose()
//		if (enabled) {
//			poseStack.translate(0.5, 0.5, 0.5)
//			poseStack.mulPose(Axis.YN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
//			poseStack.mulPose(Axis.XN.rotationDegrees(Math.floorMod(level.gameTime, 360) + partialTick))
//			poseStack.scaleFlat(0.95f)
//			localClient.blockRenderer.modelRenderer.renderModel(
//				poseStack.last(),
//				bufferSource.getBuffer(RenderType.solid()),
//				blockEntity.blockState,
//				this.starModel,
//				1f,
//				1f,
//				1f,
//				LightTexture.FULL_BRIGHT,
//				packedOverlay,
//				ModelData.EMPTY,
//				RenderType.solid()
//			)
//		}
//		poseStack.popPose()
//	}
//}