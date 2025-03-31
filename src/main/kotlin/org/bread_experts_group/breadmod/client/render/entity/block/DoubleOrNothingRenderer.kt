package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.joml.Vector3f
import org.joml.Vector4f

// todo on the backburner until i figure out the shader code...
class DoubleOrNothingRenderer(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntity> {
	override fun render(
		blockEntity: DoubleOrNothingBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		drawQuad(
			poseStack,
			bufferSource,
			ModRenderType.solidTextured(modLocation("textures/block/bread_block.png")),
			Vector4f(1f, 1f, 1f, 1f),
			Vector3f(1f, 0f, 0f),
			Vector3f(0f, 0f, 0f),
			Vector3f(1f, -2f, 0f),
			Vector3f(0f, -2f, 0f),
			1f, 1f, 1f, 1f,
			15728880
		)
		poseStack.popPose()
	}
}