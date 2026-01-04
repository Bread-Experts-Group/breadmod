package org.bread_experts_group.breadmod.experimental.camera_viewer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.solidColorTexture
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.Color

class CameraViewerBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	companion object {
		var viewerBeingRendered: CameraViewerBlockRenderer? = null
	}

	private val bg: ResourceLocation = solidColorTexture(0, 0, 0, "viewer_bg")

	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		if (Companion.viewerBeingRendered == this) return
		Companion.viewerBeingRendered = this
		val state = blockEntity.blockState
		val powered = state.getValue(BlockStateProperties.POWERED)
		val mainCamera = localClient.gameRenderer.mainCamera.position
		val pos = blockEntity.blockPos.center
		val shouldTick = pos.closerThan(mainCamera, this.viewDistance.toDouble())
		val texture = CameraTexture.get(blockEntity, 400, 400, shouldTick)
		if (texture != null && !texture.initialized) texture.init()
		val textureLoc = if (!powered) this.bg else texture?.location ?: MissingTextureAtlasSprite.getLocation()

		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		poseStack.scaleFlat(14.05f / 16)
		poseStack.translateDiv16(1.1f, -1.1f, -0.001f)
		drawQuad(poseStack, bufferSource, RenderType.text(textureLoc))
		poseStack.popPose()
		Companion.viewerBeingRendered = null
	}

	override fun renderGuiGraphics(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		guiGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
		val state = blockEntity.blockState
		val powered = state.getValue(BlockStateProperties.POWERED)
		val color = if (powered) Color.GREEN else Color.RED
		guiGraphics.fill(13, 15, 14, 16, 0, color)
	}
}