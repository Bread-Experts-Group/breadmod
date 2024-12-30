package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity

class MicrowaveRenderer : BlockEntityRenderer<MicrowaveBlockEntity> {
	override fun render(
		blockEntity : MicrowaveBlockEntity,
		partialTick : Float,
		poseStack : PoseStack,
		bufferSource : MultiBufferSource,
		packedLight : Int,
		packedOverlay : Int
	) {
		TODO("Not yet implemented")
	}
}