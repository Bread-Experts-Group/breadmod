package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.drawTextOnSide
import org.bread_experts_group.breadmod.experimental.computer.BinaryUtil.hex
import org.bread_experts_group.breadmod.registry.ModFonts
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity
import java.awt.Color

class BreadScreenRenderer(context: Context) : BreadModBER<BreadScreenBlockEntity>(context) {
	companion object {
		var output: String = "Bread BIOS v1.0\nStarting up... (DL = 0xE0, CD)\n\n"
	}

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
		Companion.output.split('\n').forEachIndexed { i, s ->
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
		listOf(
			BreadScreenBlockEntity.processor.a,
			BreadScreenBlockEntity.processor.c,
			BreadScreenBlockEntity.processor.d,
			BreadScreenBlockEntity.processor.b,
			BreadScreenBlockEntity.processor.sp,
			BreadScreenBlockEntity.processor.bp,
			BreadScreenBlockEntity.processor.si,
			BreadScreenBlockEntity.processor.di,
			BreadScreenBlockEntity.processor.ip,
			BreadScreenBlockEntity.processor.flags,
			BreadScreenBlockEntity.processor.cs,
			BreadScreenBlockEntity.processor.ss,
			BreadScreenBlockEntity.processor.ds,
			BreadScreenBlockEntity.processor.es,
			BreadScreenBlockEntity.processor.fs,
			BreadScreenBlockEntity.processor.gs,
			BreadScreenBlockEntity.processor.cr0
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