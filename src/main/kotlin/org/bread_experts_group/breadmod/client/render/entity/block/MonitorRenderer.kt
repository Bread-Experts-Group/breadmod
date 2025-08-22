package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.ModFonts
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ComputerHandler
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import org.bread_experts_group.computer.ia32.bios.h10.TeletypeOutput
import java.awt.Color

class MonitorRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	val frostOverlay: RenderType = ModRenderType.translucentTex(
		modLocation("textures/block/monitor/front_frost.png")
	)

	override fun renderGuiGraphics(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		guiGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
		val monitorComputer = blockEntity.getCapability(ComputerHandler.BLOCK_VOID)
		guiGraphics.fill(2, 2, 14, 14, 0, Color.BLACK.rgb)
		guiGraphics.fill(
			13, 15, 14, 16, 0,
			when (monitorComputer.computerStepper.state) {
				Thread.State.NEW -> Color.YELLOW
				Thread.State.TIMED_WAITING -> Color.LIGHT_GRAY
				Thread.State.WAITING -> Color.GRAY
				Thread.State.BLOCKED -> Color.ORANGE
				Thread.State.RUNNABLE -> Color.GREEN
				Thread.State.TERMINATED -> Color.RED
				else -> Color.WHITE
			}.rgb
		)
		val bios = monitorComputer.computer.bios as StandardBIOS
		for (x in 0u ..< bios.teletype.rows) {
			for (y in 0u ..< bios.teletype.cols) {
				val data = monitorComputer.computer.getMemoryAt16(
					TeletypeOutput.COLOR_ADDR + (((y * bios.teletype.rows) + x) * 2u)
				)
				val character = Char(data shr 8)
				poseStack.drawTextOnBlockSide(
					this.context.font,
					Component.literal(character.toString()).withStyle(ModFonts.IBM_VGA_9_14),
					0.13 + (0.0925 * x.toDouble()), (y.toDouble() * -0.125) - 0.13,
					bufferSource = bufferSource,
					blockState = blockEntity.blockState,
					scale = 0.017f,
					color = Color.LIGHT_GRAY.rgb
				)
			}
		}
		val processor = monitorComputer.computer.processor as IA32Processor
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
				0.2,
				-0.125 + (i * 0.15),
				bufferSource = bufferSource,
				blockState = blockEntity.blockState,
				scale = 0.015f,
			)
		}
		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState, posZ = 0.001)
		val forwardLight = blockEntity.level?.let {
			LevelRenderer.getLightColor(
				it,
				blockEntity.blockPos.relative(blockEntity.blockState.getValue(HORIZONTAL_FACING))
			)
		} ?: FULL_BRIGHT
		drawQuad(
			poseStack,
			bufferSource,
			this.frostOverlay,
			packedLight = forwardLight
		)
		poseStack.popPose()
	}
}