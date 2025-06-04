package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemInWorldBlockEntity
import kotlin.jvm.optionals.getOrNull

class ItemInWorldRenderer(context: Context) : BreadModBER<ItemInWorldBlockEntity>(context) {
	override fun render(
		blockEntity: ItemInWorldBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val direction = blockEntity.blockState.getOptionalValue(BlockStateProperties.FACING).getOrNull() ?: return
		poseStack.pushPose()
		poseStack.scaleFlat(0.5f)
		when (direction) {
			DOWN  -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(180f))
				poseStack.translate(0f, -2f, -2f)
			}
			UP    -> {}
			NORTH -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.ZN.rotationDegrees(180f))
				poseStack.translate(-2f, -2f, -2f)
			}
			SOUTH -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.translate(0f, 0f, -2f)
			}
			WEST  -> {
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.ZN.rotationDegrees(270f))
				poseStack.translate(0f, -2f, -2f)
			}
			EAST  -> {
				poseStack.mulPose(Axis.YN.rotationDegrees(-90f))
				poseStack.mulPose(Axis.XN.rotationDegrees(-90f))
				poseStack.translate(-2f, 0f, -2f)
			}
		}
		when (blockEntity.itemHandler.filledSlots) {
			1 -> {
				this.renderItem(
					blockEntity.getItem(0), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
			}
			2 -> {
				poseStack.translate(-0.5f, 0f, 0f)
				this.renderItem(
					blockEntity.getItem(0), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(1f, 0f, 0f)
				this.renderItem(
					blockEntity.getItem(1), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
			}
			3 -> {
				poseStack.translate(-0.5f, 0f, -0.5f)
				this.renderItem(
					blockEntity.getItem(0), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(1f, 0f, 0f)
				this.renderItem(
					blockEntity.getItem(1), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(-0.5f, 0f, 1f)
				this.renderItem(
					blockEntity.getItem(2), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
			}
			4 -> {
				poseStack.translate(-0.5f, 0f, -0.5f)
				this.renderItem(
					blockEntity.getItem(0), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(1f, 0f, 0f)
				this.renderItem(
					blockEntity.getItem(1), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(-1f, 0f, 1f)
				this.renderItem(
					blockEntity.getItem(2), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
				poseStack.translate(1f, 0f, 0f)
				this.renderItem(
					blockEntity.getItem(3), packedLight, packedOverlay, poseStack, bufferSource, blockEntity
				)
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
		entity: ItemInWorldBlockEntity,
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

	override fun shouldRender(entity: ItemInWorldBlockEntity, cameraPos: Vec3): Boolean = !entity.itemHandler.isEmpty()
}