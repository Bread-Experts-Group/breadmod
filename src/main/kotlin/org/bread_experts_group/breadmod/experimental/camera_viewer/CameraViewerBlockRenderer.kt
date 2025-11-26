package org.bread_experts_group.breadmod.experimental.camera_viewer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class CameraViewerBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	companion object {
		var renderer: CameraViewerBlockRenderer? = null
	}

	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val texture = CameraTexture.get(blockEntity, 400, 400)
		if (texture != null && !texture.initialized) texture.init()
		val textureLoc = texture?.location ?: MissingTextureAtlasSprite.getLocation()

		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		poseStack.scaleFlat(14.05f / 16)
		poseStack.translateDiv16(1.1f, -1.1f, 0f)
		drawQuad(poseStack, bufferSource, RenderType.text(textureLoc))
		poseStack.popPose()
	}
}