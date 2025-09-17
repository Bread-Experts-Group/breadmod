package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.AABB
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderTextNoBg
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.Registry.playingSounds
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker
import org.bread_experts_group.breadmod.util.Color

class RadioRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context, false) {
	override fun renderBM(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		poseStack.drawTextOnBlockSide(
			localClient.font,
			Component.literal("a string of text"),
			0.0,
			0.0,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.01f
		)
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
		val soundInstance = playingSounds[blockEntity.blockPos.center] as? StereoSoundInstance ?: return

		this.positionDisplay(poseStack, blockEntity, partialTick)
		this.drawBg(poseStack, guiGraphics)
		this.drawImage(poseStack, guiGraphics, soundInstance)
		this.drawTitle(poseStack, bufferSource, soundInstance)
		this.drawArtist(poseStack, bufferSource, soundInstance)

		poseStack.pushPose()
		poseStack.pushForward(3)
		guiGraphics.fillPositioned(2, 4, this.scaledProgress(soundInstance), 1, Color.GREEN)
		poseStack.popPose()

		poseStack.mulPose(Axis.YP.rotationDegrees(180f))

		this.drawBg(poseStack, guiGraphics)
		this.drawImage(poseStack, guiGraphics, soundInstance)
	}

	private fun scaledProgress(instance: StereoSoundInstance): Int {
		val stream = instance.stream
		return ((stream.currentSlice.toFloat() / stream.dataSize.toFloat()) * 28f).toInt()
	}

	private fun positionDisplay(poseStack: PoseStack, entity: BreadModBlockEntity, partialTick: Float) {
		poseStack.translate(-8, -20, 8)
		poseStack.translate(16, 8, 0)
		poseStack.mulPose(Axis.YP.rotationDegrees(90f))
		val lerpTicker = entity.getLerpTicker<LerpLabels>()
		poseStack.mulPose(Axis.YP.rotation(lerpTicker.getLerpedValue(LerpLabels.TILT_BETA, partialTick)))
		poseStack.mulPose(Axis.YP.rotation(lerpTicker.getLerpedValue(LerpLabels.TILT_ALPHA, partialTick)))
	}

	private fun PoseStack.pushForward(factor: Int) {
		this.translate(0f, 0f, -0.005f * factor)
	}

	private fun drawBg(poseStack: PoseStack, guiGraphics: GuiGraphics) {
		poseStack.pushPose()
		poseStack.translate(-20, -10, 0)
		guiGraphics.fillPositioned(0f, 0f, 40f, 20f, Color.WHITE)
		poseStack.pushForward(1)
		guiGraphics.fillPositioned(0.5f, 0.5f, 39.5f, 19.5f, Color.BLACK)
		poseStack.popPose()
	}

	private fun drawImage(poseStack: PoseStack, guiGraphics: GuiGraphics, instance: StereoSoundInstance) {
		val image = instance.stream.image
		poseStack.pushPose()
		poseStack.pushForward(2)
		poseStack.scaleFlat(0.2f)
		guiGraphics.blit(image.location, -95, -45, 0f, 0f, 64, 64, 64, 64)
		poseStack.popPose()
	}

	private fun drawTitle(poseStack: PoseStack, bufferSource: MultiBufferSource, instance: StereoSoundInstance) {
		val title = instance.stream.title
		val fileName = instance.fileName
		poseStack.pushPose()
		poseStack.pushForward(10)
		poseStack.translate(-5.5, -9.0, 0.0)
		poseStack.scaleFlat(0.3f)
		this.renderText(if (title == Component.empty()) fileName else title, poseStack, bufferSource)
		poseStack.popPose()
	}

	private fun drawArtist(poseStack: PoseStack, bufferSource: MultiBufferSource, instance: StereoSoundInstance) {
		val artist = instance.stream.artist
		poseStack.pushPose()
		poseStack.pushForward(10)
		poseStack.translate(-5.5, -6.0, 0.0)
		poseStack.scaleFlat(0.3f)
		this.renderText(artist, poseStack, bufferSource)
		poseStack.popPose()
	}

	private fun renderText(component: Component, poseStack: PoseStack, bufferSource: MultiBufferSource) {
		val comp = if (component == Component.empty()) Component.literal("<unknown>").visualOrderText
		else component.visualOrderText

		localClient.font.renderTextNoBg(
			comp,
			Color.WHITE,
			poseStack,
			bufferSource,
			true,
			LightTexture.FULL_BRIGHT,
			dropShadowOffset = -0.03f
		)
	}

	private fun drawText(poseStack: PoseStack, guiGraphics: GuiGraphics, instance: StereoSoundInstance) {
		val stream = instance.stream
		val size = stream.dataSize
		poseStack.pushPose()
		poseStack.translate(2.0, 2.5, 0.0)
		poseStack.scaleFlat(0.15f)
		guiGraphics.drawString(localClient.font, "${stream.currentSlice} / $size", 0, 0, Color.WHITE, false)
		poseStack.popPose()
	}

	override fun getRenderBoundingBox(blockEntity: BreadModBlockEntity): AABB {
		val aabb = super.getRenderBoundingBox(blockEntity)
		return aabb.expandTowards(0.0, aabb.ysize, 0.0)
	}

	// TODO: chris review these names
	enum class LerpLabels {
		TILT_ALPHA,
		TILT_BETA
	}
}