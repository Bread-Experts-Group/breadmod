package org.bread_experts_group.breadmod.client.tool_gun.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.transparentColor
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.caseOhInstrument
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.caseOhSize
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.coilDelta
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.coilModel
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.coilRotation
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.helper
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.mainModel
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.recoil
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.formatNumberBigDecimal
import java.awt.Color
import java.lang.Math.clamp
import java.math.BigDecimal
import java.math.RoundingMode

class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	override fun onResourceManagerReload(resourceManager: ResourceManager) {
		helper = ToolGunRenderHelper()
	}

	private fun renderToolGun(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		currentMode: IToolGunMode,
		helper: ToolGunRenderHelper
	) {
		val deltaTracker = localClient.timer
		val partialTick = deltaTracker.gameTimeDeltaTicks
		if (coilDelta > 0f) {
			coilDelta -= 0.025f * partialTick
			coilRotation += (40f * coilDelta) * partialTick
			recoil -= 0.025f * partialTick * coilDelta / 3.5f
		}

		if (displayContext.firstPerson()) {
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (helper.shouldRecoil) poseStack.translate(-clamp(recoil, 0f, 1f), 0f, 0f)

			poseStack.pushPose()
			currentMode.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, helper)
			// Render Body Stage
			if (helper.shouldRenderMainBody) helper.itemRenderer.renderItemModel(
				mainModel,
				stack,
				displayContext,
				poseStack,
				buffer,
				packedOverlay,
				packedLight
			)
			currentMode.renderBodyStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, helper)
			poseStack.popPose()
			// Render Screen Stage
			poseStack.pushPose()
			if (helper.shouldRenderScreenContents) {
				helper.renderScreenBackground(ModTextureLocations.SCREEN.location, 9, 8, poseStack, buffer)
				helper.drawTextOnScreen(
					currentMode.getDisplayName(),
					Color.RED.rgb, transparentColor().rgb,
					false,
					helper.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.414
				)
				caseOhSize = caseOhSize.add(BigDecimal.valueOf(caseOhInstrument.nextDouble(0.0, 1234511121314.0)))
				val (truncated, unit) = formatNumberBigDecimal(caseOhSize)
				helper.drawTextOnScreen(
					"CASEOH: ${truncated.setScale(2, RoundingMode.DOWN)} ${unit}g",
					Color.RED.rgb,
					transparentColor().rgb,
					false,
					helper.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.361
				)
			}
			currentMode.renderScreenStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, helper)
			poseStack.popPose()
			// Render Coil Stage
			poseStack.pushPose()
			if (currentMode.shouldCoilSpin(stack, displayContext)) {
				poseStack.mulPose(Axis.XN.rotationDegrees(coilRotation))
			}
			if (helper.shouldRenderCoil) helper.itemRenderer.renderItemModel(
				coilModel,
				stack,
				displayContext,
				poseStack,
				buffer,
				packedOverlay,
				packedLight
			)
			currentMode.renderCoilStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, helper)
			poseStack.popPose()
			// End Render, pop the final push
			poseStack.popPose()
		} else if (displayContext == GUI) {
			this.renderOtherPerspectives(stack, displayContext, poseStack, buffer, packedOverlay, packedLight, true)
		} else {
			this.renderOtherPerspectives(stack, displayContext, poseStack, buffer, packedOverlay, packedLight, false)
		}
	}

	private fun renderOtherPerspectives(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedOverlay: Int,
		packedLight: Int,
		coilSpin: Boolean
	) {
		helper.itemRenderer.renderItemModel(
			mainModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)
		if (coilSpin) poseStack.mulPose(Axis.XN.rotationDegrees(coilRotation))
		helper.itemRenderer.renderItemModel(
			coilModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)
	}

	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val currentMode = stack.get(ModDataComponents.TOOL_GUN_DATA) ?: EmptyMode()
		this.renderToolGun(
			stack,
			displayContext,
			poseStack,
			buffer,
			packedLight,
			packedOverlay,
			currentMode,
			helper
		)
	}
}