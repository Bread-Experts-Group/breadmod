package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.world.phys.AABB
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.playingSounds
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.block.actual.entity.RadioBlockEntity
import org.bread_experts_group.breadmod.util.Color

class RadioRenderer(context: Context) : BreadModBER<RadioBlockEntity>(context, false) {
	override fun renderWithGraphics(
		blockEntity: RadioBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		lgPoseStack: PoseStack,
		bufferSource: MultiBufferSource,
		levelGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
		val soundInstance = playingSounds[blockEntity.blockPos] ?: return
		val pointer = soundInstance.mp3Stream.currentSlice
		val size = soundInstance.mp3Stream.dataSize
		val player = localClient.player ?: return
		lgPoseStack.translate(-8, -20, 8)
		lgPoseStack.translate(16, 8, 0)
		lgPoseStack.mulPose(Axis.YP.rotationDegrees(player.getViewYRot(partialTick)))
		lgPoseStack.mulPose(Axis.XN.rotationDegrees(player.xRot))
		lgPoseStack.translate(-16, -8, 0)
		levelGraphics.fillPositioned(0, 0, 32, 16, Color.WHITE)
		lgPoseStack.translate(0f, 0f, -0.05f)
		levelGraphics.fillPositioned(1, 1, 30, 14, Color.BLACK)
		lgPoseStack.translate(0f, 0f, -0.05f)
		lgPoseStack.pushPose()
		lgPoseStack.translate(2.0, 2.5, 0.0)
		lgPoseStack.scaleFlat(0.15f)
		levelGraphics.drawString(localClient.font, "$pointer / $size", 0, 0, Color.WHITE, false)
		lgPoseStack.popPose()
		levelGraphics.fillPositioned(2, 4, this.scaledProgress(soundInstance), 1, Color.GREEN)
	}

	private fun scaledProgress(instance: StereoSoundInstance): Int {
		val stream = instance.mp3Stream
		return ((stream.currentSlice.toFloat() / stream.dataSize.toFloat()) * 28f).toInt()
	}

	override fun getRenderBoundingBox(blockEntity: RadioBlockEntity): AABB {
		val aabb = super.getRenderBoundingBox(blockEntity)
		return aabb.expandTowards(0.0, aabb.ysize, 0.0)
	}
}