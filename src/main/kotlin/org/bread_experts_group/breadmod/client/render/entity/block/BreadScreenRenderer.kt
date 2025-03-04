package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.drawTextOnSide
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.experimental.computer.ia32.IA32Processor
import org.bread_experts_group.breadmod.registry.ModFonts
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity
import java.awt.Color

class BreadScreenRenderer(context: Context) : BreadModBER<BreadScreenBlockEntity>(context) {
	override fun renderWithGraphics(
		blockEntity: BreadScreenBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		lgPoseStack: PoseStack,
		bufferSource: MultiBufferSource,
		levelGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
		levelGraphics.fill(1, 1, 14, 14, 1, Color.BLACK.rgb)
		blockEntity.computer.buffer.split('\n').forEachIndexed { i, s ->
			poseStack.drawTextOnSide(
				this.context.font,
				Component.literal(s).withStyle(ModFonts.IBM_EGA_9_14),
				0.065, (-i * 0.025) - 0.07,
				bufferSource = bufferSource,
				blockState = blockEntity.blockState,
				scale = 0.0025f,
				color = Color.LIGHT_GRAY.rgb
			)
		}
		val processor = blockEntity.computer.processor as IA32Processor
		listOf(
			processor.a,
			processor.c,
			processor.d,
			processor.b,
			processor.sp,
			processor.bp,
			processor.si,
			processor.di,
			processor.ip,
			processor.flags,
			processor.cs,
			processor.ss,
			processor.ds,
			processor.es,
			processor.fs,
			processor.gs,
			processor.cr0
		).forEachIndexed { i, r ->
			val component = Component.literal("${r.name}: ${hex(r.rx)}").withStyle(ModFonts.IBM_EGA_9_14)
			poseStack.drawTextOnSide(
				this.context.font,
				component,
				0.66, (-i * 0.015) - 0.07,
				bufferSource = bufferSource,
				blockState = blockEntity.blockState,
				scale = 0.0015f,
				color = Color.ORANGE.rgb
			)
		}
	}
}