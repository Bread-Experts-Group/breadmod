package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.modelLocation
import org.bread_experts_group.breadmod.util.render.renderBlockModel
import org.bread_experts_group.breadmod.util.render.renderStaticItem
import org.bread_experts_group.breadmod.util.render.scaleFlat

class ToasterRenderer(private val ctx : Context) : BlockEntityRenderer<ToasterBlockEntity> {
	private companion object {
		val HANDLE_MODEL_LOC = modelLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
		val HANDLE_MODEL : BakedModel = localClient.modelManager.getModel(this.HANDLE_MODEL_LOC)
	}

	private var triggeredOffset : Double = 0.0
	private val blockModelRenderer = this.ctx.blockRenderDispatcher.modelRenderer
	private val itemRenderer = this.ctx.itemRenderer

	override fun render(
		blockEntity : ToasterBlockEntity,
		partialTick : Float,
		poseStack : PoseStack,
		bufferSource : MultiBufferSource,
		packedLight : Int,
		packedOverlay : Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

		this.triggeredOffset = if (triggered) -0.13 else 0.0

		poseStack.pushPose()
		when (blockRotation) {
			Direction.SOUTH -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(180f))
				poseStack.translate(-1.0, this.triggeredOffset, -1.0)
			}

			Direction.WEST  -> {
				poseStack.translate(0.0, this.triggeredOffset, 1.0)
				poseStack.mulPose(Axis.YN.rotationDegrees(-90f))
			}

			Direction.EAST  -> {
				poseStack.translate(1.0, this.triggeredOffset, 0.0)
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			}

			Direction.NORTH -> {
				poseStack.translate(0.0, this.triggeredOffset, 0.0)
			}

			else -> {}
		}

		this.blockModelRenderer.renderBlockModel(
			poseStack.last(),
			bufferSource,
			blockEntity,
			Companion.HANDLE_MODEL,
			packedLight,
			packedOverlay,
			Sheets.solidBlockSheet()
		)
		poseStack.popPose()
		val items = blockEntity.items ?: return
		val stack = items.getStackInSlot(0)

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

	override fun getViewDistance() : Int = 32
}