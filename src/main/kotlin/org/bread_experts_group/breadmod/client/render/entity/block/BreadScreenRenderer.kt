package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
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
		levelGraphics.fill(0, 0, 16, 16, 1, Color.BLACK.rgb)
		lgPoseStack.scaleFlat(0.025f)
		Companion.output.split('\n').forEachIndexed { i, s ->
			levelGraphics.drawString(
				localClient.font,
				Component.literal(s).withStyle(ModFonts.IBM_EGA_9_14),
				0, i * 9, Color.LIGHT_GRAY.rgb, false
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
			levelGraphics.drawString(
				localClient.font, component,
				localClient.window.guiScaledWidth - localClient.font.width(component), i * 9,
				Color.ORANGE.rgb, false
			)
		}
	}
}