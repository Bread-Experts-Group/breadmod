package org.bread_experts_group.breadmod.client.render.entity.block
//class EnergyStorageRenderer(context: Context) : BreadModBER<EnergyStorageBlockEntity>(context) {
//	override fun renderBM(
//		blockEntity: EnergyStorageBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val energyStored = blockEntity.energyHandler.energyStored
//		val maxEnergyStored = blockEntity.energyHandler.maxEnergyStored
//		poseStack.drawTextOnBlockSide(
//			this.context.font,
//			Component.literal("$energyStored FE"),
//			0.1,
//			-0.125,
//			bufferSource = bufferSource,
//			blockState = blockEntity.blockState,
//			scale = 0.0105f,
//			color = Color.GREEN.rgb
//		)
//		poseStack.drawTextOnBlockSide(
//			this.context.font,
//			Component.literal("-------------"),
//			0.095,
//			-0.185,
//			bufferSource = bufferSource,
//			blockState = blockEntity.blockState,
//			scale = 0.0105f,
//			color = Color.GREEN.rgb
//		)
//		poseStack.drawTextOnBlockSide(
//			this.context.font,
//			Component.literal("$maxEnergyStored FE"),
//			0.1,
//			-0.245,
//			bufferSource = bufferSource,
//			blockState = blockEntity.blockState,
//			scale = 0.0105f,
//			color = Color.GREEN.rgb
//		)
//		super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay)
//	}
//}