package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.registry.ModFonts
import org.bread_experts_group.breadmod.registry.block.actual.entity.MonitorBlockEntity
import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.h10.TeletypeOutput
import java.awt.Color

class MonitorRenderer(context: Context) : BreadModBER<MonitorBlockEntity>(context) {
	override fun renderGuiGraphics(
		blockEntity: MonitorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		guiGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
		guiGraphics.fill(2, 2, 14, 14, 1, Color.BLACK.rgb)
		guiGraphics.fill(
			13, 15, 14, 16, 1,
			when (blockEntity.computerStepper.state) {
				Thread.State.NEW           -> Color.YELLOW
				Thread.State.TIMED_WAITING -> Color.LIGHT_GRAY
				Thread.State.WAITING       -> Color.GRAY
				Thread.State.BLOCKED       -> Color.ORANGE
				Thread.State.RUNNABLE      -> Color.GREEN
				Thread.State.TERMINATED    -> Color.RED
				else                       -> Color.WHITE
			}.rgb
		)
		for (x in (0u).toULong() ..< TeletypeOutput.ROWS) {
			for (y in (0u).toULong() ..< TeletypeOutput.COLS) {
				val data = blockEntity.computer.requestMemoryAt16(
					TeletypeOutput.COLOR_ADDR + (((y * TeletypeOutput.ROWS) + x) * 2u)
				)
				val character = Char(data shr 8)
//				val color = data and 0xFu
				poseStack.drawTextOnBlockSide(
					this.context.font,
					Component.literal(character.toString()).withStyle(ModFonts.IBM_VGA_9_14),
					0.13 + (0.00925 * x.toDouble()), (y.toDouble() * -0.0125) - 0.13,
					bufferSource = bufferSource,
					blockState = blockEntity.blockState,
					scale = 0.0017f,
					color = Color.LIGHT_GRAY.rgb
				)
			}
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
			val component = Component.literal("${r.name}: ${hex(r.rx)}").withStyle(ModFonts.IBM_VGA_9_14)
			poseStack.drawTextOnBlockSide(
				this.context.font,
				component,
				0.66, (-i * 0.015) + 0.5,
				bufferSource = bufferSource,
				blockState = blockEntity.blockState,
				scale = 0.0015f,
				color = Color.ORANGE.rgb
			)
		}
		// TODO figure out how to render frost
//		RenderSystem.setShader(GameRenderer::getRendertypeGlintShader)
//		levelGraphics.blit(
//			modLocation("textures", "block", "monitor_face.png"),
//			2, 2,
//			2f, 2f,
//			12, 12,
//			16, 16
//		)
	}
}