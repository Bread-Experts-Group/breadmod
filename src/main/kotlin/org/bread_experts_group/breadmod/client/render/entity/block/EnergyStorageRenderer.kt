package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.util.Color

class EnergyStorageRenderer(
	private val context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<BreadModBlockEntity> {
	override fun render(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val energy = blockEntity.getCapability(Capabilities.EnergyStorage.BLOCK) as ExtendedEnergyHandler
		val energyStored = energy.bigAmount
		val maxEnergyStored = energy.bigCapacity
		poseStack.drawTextOnBlockSide(
			this.context.font,
			Component.literal("$energyStored FE"),
			0.1,
			-0.125,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN
		)
		poseStack.drawTextOnBlockSide(
			this.context.font,
			Component.literal("-------------"),
			0.095,
			-0.185,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN
		)
		poseStack.drawTextOnBlockSide(
			this.context.font,
			Component.literal("$maxEnergyStored FE"),
			0.1,
			-0.245,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			color = Color.GREEN
		)
	}
}