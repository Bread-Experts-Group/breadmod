package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon
import org.bread_experts_group.breadmod.mixinutil.RenderGeneral
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.EnergyStorageStateHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.EnergyStorageStateHandler.Companion.COLOR
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.floatRoundEven
import org.joml.Vector3f
import java.math.BigDecimal
import kotlin.math.roundToInt

class EnergyStorageRenderer(
	private val context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<BreadModBlockEntity> {
	val glowOverlayHeight: Float = 16f
	val frostOverlay: RenderType = ModRenderType.translucentTex(
		modLocation("textures/block/energy_storage/front_frost.png")
	)
	val glowOverlay: RenderType = ModRenderType.glow(
		modLocation("textures/block/energy_storage/front_overlay_thirteen.png")
	)

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
		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		val storageState = blockEntity.getCapability(EnergyStorageStateHandler.BLOCK_VOID)
		val energyScaled = (energyStored.divide(maxEnergyStored, floatRoundEven).toFloat() * this.glowOverlayHeight)
			.roundToInt()
		val yNormalized = -1f + (energyScaled / this.glowOverlayHeight)
		val color = storageState.get(COLOR)
		drawQuad(
			poseStack,
			bufferSource,
			this.glowOverlay,
			color,
			topLeft = Vector3f(0f, yNormalized, 0f),
			topRight = Vector3f(1f, yNormalized, 0f),
			v0 = ((this.glowOverlayHeight - energyScaled) / this.glowOverlayHeight)
		)
		poseStack.popPose()
		val amount = JadeDrawingCommon.fixedLengthScrollingComponentSinglet(
			energyStored, "FE",
			color
		)
		val light = if (energyStored == BigDecimal.ZERO) packedLight else FULL_BRIGHT
		poseStack.drawTextOnBlockSide(
			this.context.font,
			amount,
			0.2,
			-0.125,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			packedLight = light
		)
		val capacity = JadeDrawingCommon.fixedLengthScrollingComponentSinglet(
			maxEnergyStored, "FE",
			color
		)
		poseStack.drawTextOnBlockSide(
			this.context.font,
			capacity,
			0.2,
			-0.245,
			bufferSource = bufferSource,
			blockState = blockEntity.blockState,
			scale = 0.0105f,
			packedLight = light
		)
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
		RenderGeneral.renderBlend(localClient.timer)
		poseStack.popPose()
	}
}