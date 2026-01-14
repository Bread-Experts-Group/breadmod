package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.util.RandomSource
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.client.render.LevelGraphics
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.tessellateModel
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.Color

/**
 * Bread Mod Specific [BlockEntityRenderer] with an extremely cursed "in-world" [GuiGraphics] implementation.
 */
abstract class BreadModBER(
	val context: BlockEntityRendererProvider.Context,
	private val snapGraphicsToBlockSide: Boolean = true
) : BlockEntityRenderer<BreadModBlockEntity> {
	companion object {
		const val TRANSLATE_OFFSET: Double = 0.0001
	}

	protected val random: RandomSource = RandomSource.create()
	private val modelData: ModelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()
//	private val debugAxisModel: BakedModel = localClient.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/axis")
	protected fun renderModel(
		blockEntity: BreadModBlockEntity,
		model: BakedModel,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		localClient.blockRenderer.modelRenderer.tessellateModel(
			blockEntity,
			model,
			poseStack,
			bufferSource,
			this.random,
			packedOverlay,
			this.modelData
		)
		poseStack.popPose()
	}

	//	protected fun renderDebugAxis(blockEntity: T, poseStack: PoseStack, bufferSource: MultiBufferSource) {
//		this.renderModel(
//			blockEntity,
//			this.debugAxisModel,
//			poseStack,
//			bufferSource,
//			OverlayTexture.NO_OVERLAY
//		)
//	}

	final override fun render(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val levelGraphics = LevelGraphics()
		levelGraphics.pose().last().pose().set(poseStack.last().pose())
		// todo band-aid fix. replace with own innerBlit and override blit methods to use it.
		levelGraphics.fill(0, 0, 0, 0, Color.color(a = 0))
		if (this.snapGraphicsToBlockSide) levelGraphics.translateToBlockSide(blockEntity)
		levelGraphics.setup()
		this.renderGuiGraphics(
			blockEntity,
			partialTick,
			levelGraphics.pose(),
			levelGraphics.bufferSource(),
			levelGraphics,
			packedLight,
			packedOverlay
		)
		levelGraphics.teardown()
		this.renderBM(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay)
	}

	open fun renderGuiGraphics(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		guiGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	open fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}
}