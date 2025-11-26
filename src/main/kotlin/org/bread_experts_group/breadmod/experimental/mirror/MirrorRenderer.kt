package org.bread_experts_group.breadmod.experimental.mirror

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class MirrorRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	companion object {
		val blockEntities: MutableList<BreadModBlockEntity> = mutableListOf()
		val textures: MutableMap<BlockPos, MirrorTexture> = mutableMapOf()
	}

	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		if (blockEntity !in Companion.blockEntities) Companion.blockEntities.add(blockEntity)
		val texture = Companion.textures[blockEntity.blockPos] ?: return

		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		drawQuad(poseStack, bufferSource, RenderType.text(texture.location))
		poseStack.popPose()
	}
}