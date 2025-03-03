package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.drawTextOnSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.storage.EnergyStorageBlockEntity
import java.awt.Color

class EnergyStorageRenderer(context: Context) : BreadModBER<EnergyStorageBlockEntity>(context) {
	override fun render(
		blockEntity: EnergyStorageBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val energyStored = blockEntity.energyHandler.energyStored
		val maxEnergyStored = blockEntity.energyHandler.maxEnergyStored
		poseStack.drawTextOnSide(
			this.context.font,
			Component.literal("$energyStored FE"),
			0.1,
			-0.125,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN.rgb
		)
		poseStack.drawTextOnSide(
			this.context.font,
			Component.literal("-------------"),
			0.095,
			-0.185,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN.rgb
		)
		poseStack.drawTextOnSide(
			this.context.font,
			Component.literal("$maxEnergyStored FE"),
			0.1,
			-0.245,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN.rgb
		)
	}
}