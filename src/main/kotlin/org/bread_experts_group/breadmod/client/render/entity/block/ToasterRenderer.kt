package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderStaticItem
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedItemHandler
import java.math.BigDecimal

class ToasterRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	private companion object {
		val HANDLE_MODEL: BakedModel = localClient.getModel("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
	}

	private val itemRenderer: ItemRenderer = this.context.itemRenderer

	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

		poseStack.pushPose()
		when (blockRotation) {
			Direction.SOUTH -> {
				poseStack.mulPose(Axis.YP.rotationDegrees(180f))
				poseStack.translate(-1.0, 0.0, -1.0)
			}
			Direction.WEST -> {
				poseStack.mulPose(Axis.YP.rotationDegrees(90f))
				poseStack.translate(-1.0, 0.0, 0.0)
			}
			Direction.EAST -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(90f))
				poseStack.translate(0.0, 0.0, -1.0)
			}
			else -> {}
		}
		poseStack.translate(0.0, if (triggered) -0.13 else 0.0, 0.0)
		this.renderModel(blockEntity, Companion.HANDLE_MODEL, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
		poseStack.pushPose()
		poseStack.translate(0.5, 0.3, 0.61)
		poseStack.scaleFlat(0.6f)
		if (blockRotation == Direction.SOUTH || blockRotation == Direction.NORTH) {
			poseStack.mulPose(Axis.YN.rotationDegrees(90f))
			poseStack.translateDiv16(-2.8, 0.0, 3.0)
		}
		if (!triggered) {
			val storage = blockEntity.getCapability(Capabilities.ItemHandler.BLOCK) as ExtendedItemHandler
			val slot = storage.slots.getValue(0)
			val stack = ItemStack(slot.item)
			if (slot.amount == BigDecimal.TWO) {
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
				poseStack.translate(0.0, 0.0, -0.37)
				this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
			} else this.itemRenderer.renderStaticItem(stack, poseStack, bufferSource, blockEntity, packedLight)
		}
		poseStack.popPose()
	}

	override fun getViewDistance(): Int = 32
}