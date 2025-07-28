package org.bread_experts_group.breadmod.client.render.entity.block
//class CableRenderer(private val context: Context) : BlockEntityRenderer<CableBlockEntity> {
//	override fun render(
//		blockEntity: CableBlockEntity,
//		partialTick: Float,
//		poseStack: PoseStack,
//		bufferSource: MultiBufferSource,
//		packedLight: Int,
//		packedOverlay: Int
//	) {
//		val handler = blockEntity.capabilities.firstNotNullOfOrNull { it.value as? IEnergyStorage } ?: return
//		val player = localClient.player ?: return
//		poseStack.translate(0.35f, 1f, 0.5f)
//		poseStack.mulPose(Axis.XN.rotationDegrees(180f))
//		poseStack.mulPose(Axis.YN.rotationDegrees(180f))
//		poseStack.mulPose(Axis.YN.rotationDegrees(-player.getViewYRot(partialTick)))
//		poseStack.mulPose(Axis.XN.rotationDegrees(player.xRot))
//		poseStack.scaleFlat(0.025f)
//		this.context.font.renderText(
//			Component.literal("${handler.energyStored}FE").visualOrderText,
//			Color.WHITE,
//			Color.BLACK,
//			poseStack,
//			bufferSource,
//			false,
//			packedLight
//		)
//	}
//}