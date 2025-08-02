package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.SlotQueueHandler
import kotlin.jvm.optionals.getOrNull

class ItemInWorldRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val direction = blockEntity.blockState.getOptionalValue(BlockStateProperties.FACING).getOrNull()
			?: return
		poseStack.pushPose()
		poseStack.scaleFlat(0.5f)
		when (direction) {
			Direction.DOWN -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(180f))
				poseStack.translate(0f, -2f, -2f)
			}
			Direction.UP -> {}
			Direction.NORTH -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.ZN.rotationDegrees(180f))
				poseStack.translate(-2f, -2f, -2f)
			}
			Direction.SOUTH -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.translate(0f, 0f, -2f)
			}
			Direction.WEST -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.ZN.rotationDegrees(270f))
				poseStack.translate(0f, -2f, -2f)
			}
			Direction.EAST -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.translate(-2f, 0f, -2f)
			}
		}
		blockEntity.level?.getCapability(SlotQueueHandler.BLOCK_VOID, blockEntity.blockPos)?.let {
			when (it.size) {
				1 -> this.renderItem(it[0], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
				2 -> {
					poseStack.translate(-0.5f, 0f, 0f)
					this.renderItem(it[0], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(1f, 0f, 0f)
					this.renderItem(it[1], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
				}
				3 -> {
					poseStack.translate(-0.5f, 0f, -0.5f)
					this.renderItem(it[0], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(1f, 0f, 0f)
					this.renderItem(it[1], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(-0.5f, 0f, 1f)
					this.renderItem(it[2], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
				}
				4 -> {
					poseStack.translate(-0.5f, 0f, -0.5f)
					this.renderItem(it[0], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(1f, 0f, 0f)
					this.renderItem(it[1], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(-1f, 0f, 1f)
					this.renderItem(it[2], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
					poseStack.translate(1f, 0f, 0f)
					this.renderItem(it[3], packedLight, packedOverlay, poseStack, bufferSource, blockEntity)
				}
			}
		}
		poseStack.popPose()
	}

	// Note: This is initially translated to the middle of the "block" before rendering
	private fun renderItem(
		stack: ItemStack,
		packedLight: Int,
		packedOverlay: Int,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		entity: BreadModBlockEntity,
	) {
		poseStack.pushPose()
		if (stack.item is BlockItem) {
			poseStack.translate(1f, 0.5f, 1f)
		} else {
			poseStack.translate(1f, 0.025f, 1f)
			poseStack.mulPose(Axis.XN.rotationDegrees(90f))
		}
		this.context.itemRenderer.renderStatic(
			stack,
			ItemDisplayContext.NONE,
			packedLight,
			packedOverlay,
			poseStack,
			bufferSource,
			entity.level,
			42
		)
		poseStack.popPose()
	}

	override fun shouldRender(entity: BreadModBlockEntity, cameraPos: Vec3): Boolean = (entity.level?.getCapability(
		SlotQueueHandler.BLOCK_VOID, entity.blockPos
	)?.size ?: 0) > 0
}