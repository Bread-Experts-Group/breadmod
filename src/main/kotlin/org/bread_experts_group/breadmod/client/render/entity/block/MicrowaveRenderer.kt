package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity

class MicrowaveRenderer(context: Context) : BreadModBER<MicrowaveBlockEntity>(context) {
	private companion object {
		val DOOR_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door")
		val PLATE_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate")
		val DOOR_MODEL: BakedModel = localClient.modelManager.getModel(this.DOOR_MODEL_LOC)
		val PLATE_MODEL: BakedModel = localClient.modelManager.getModel(this.PLATE_MODEL_LOC)
	}

	private val blockModelRenderer = this.context.blockRenderDispatcher.modelRenderer

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

		poseStack.pushPose()
		when (blockRotation) {
			Direction.NORTH -> {
				poseStack.translate(0.875f, 0.125f, 0.1935f)
			}
			else            -> {}
		}

		if (open) poseStack.mulPose(Axis.YN.rotationDegrees(102f))
		this.blockModelRenderer.tesselateWithAO(
			blockEntity.level ?: return,
			Companion.DOOR_MODEL,
			blockEntity.blockState,
			blockEntity.blockPos,
			poseStack,
			bufferSource.getBuffer(Sheets.translucentCullBlockSheet()),
			false,
			this.random,
			801234,
			packedOverlay,
			this.modelData,
			Sheets.translucentCullBlockSheet()
		)
		poseStack.popPose()
	}
}