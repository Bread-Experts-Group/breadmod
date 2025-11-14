package org.bread_experts_group.breadmod.experimental.camera

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import java.util.UUID

class CameraBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	companion object {
		val textures: MutableMap<UUID, CameraTexture> = mutableMapOf()
		var textureCounter: Int = 0
	}

	override fun render(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val handler = blockEntity.getCapability(CameraStateHandler.BLOCK_VOID)
		val texture = Companion.textures.getOrPut(handler.id) {
			CameraTexture(
				blockEntity,
				modLocation("camera_texture_${Companion.textureCounter++}")
			)
		}

		poseStack.translateOnBlockSide(blockEntity.blockState)
		drawQuad(poseStack, bufferSource, RenderType.text(texture.location))
	}
}