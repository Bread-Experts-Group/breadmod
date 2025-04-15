package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity

class ToasterRenderer(
	context: Context
) : BreadModBER<ToasterBlockEntity>(
	context,
	context.blockRenderDispatcher.getBlockModel(ModBlocks.TOASTER.asBlock().defaultBlockState())
) {
	private companion object {
		val HANDLE_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
		val HANDLE_MODEL: BakedModel = localClient.modelManager.getModel(this.HANDLE_MODEL_LOC)
	}

	private val itemRenderer = this.context.itemRenderer

	override fun render(
		blockEntity: ToasterBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

		poseStack.pushPose()
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		this.renderOriginalModel(blockEntity, poseStack, bufferSource, packedOverlay)
		poseStack.translate(0.0, if (triggered) -0.13 else 0.0, 0.0)
		this.renderModel(blockEntity, Companion.HANDLE_MODEL, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
		val stack = blockEntity.itemHandler.getStackInSlot(0)

		poseStack.pushPose()
		poseStack.translate(0.5, 0.3, 0.61)
		poseStack.scaleFlat(0.6f)
		if (blockRotation == SOUTH || blockRotation == NORTH) {
			poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			poseStack.translate(-0.185, 0.0, 0.185)
		}
		if (!triggered) {
			if (stack.count == 2) {
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
				poseStack.translate(0.0, 0.0, -0.37)
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
			} else this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
		}
		poseStack.popPose()
	}

	override fun getViewDistance(): Int = 32
}