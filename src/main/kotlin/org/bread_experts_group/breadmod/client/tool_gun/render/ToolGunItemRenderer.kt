package org.bread_experts_group.breadmod.client.tool_gun.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.transparentColor
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import java.awt.Color

class ToolGunItemRenderer :
	BlockEntityWithoutLevelRenderer(localClient.blockEntityRenderDispatcher, localClient.entityModels) {
	companion object {
		private val useAltModel = ModConfiguration.CLIENT.useAlternateToolGunModel
		private var helper = ToolGunRenderHelper.init()
		private val mainModel: BakedModel =
			this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/item"))
		private val coilModel: BakedModel =
			this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/coil"))
		private val altModel: BakedModel =
			this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt"))

		// Recoil and Coil Spin vars
		private var coilRotation: Float = 0f
		private var coilDelta: Float = 0f
		private var recoil: Float = 0f

		/**
		 * Sets the delta and recoil to their triggered values.
		 */
		fun triggerDelta() {
			this.coilDelta = 1f
			this.recoil = 0.1f
		}
	}

	override fun onResourceManagerReload(resourceManager: ResourceManager) {
		Companion.helper = ToolGunRenderHelper.init()
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
		if (Companion.coilDelta > 0f) {
			Companion.coilDelta -= 0.025f * partialTick
			Companion.coilRotation += (40f * Companion.coilDelta) * partialTick
			Companion.recoil -= 0.025f * partialTick * Companion.coilDelta / 3.5f
		}

		if (displayContext.firstPerson()) {
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (helper.shouldRecoil) poseStack.translate(-Mth.clamp(Companion.recoil, 0f, 1f), 0f, 0f)

			poseStack.pushPose()
			currentMode.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, helper)
			// Render Body Stage
			if (helper.shouldRenderMainBody) helper.itemRenderer.renderItemModel(
				Companion.mainModel,
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
				helper.renderScreenBackground(
					ModTextureLocations.SCREEN.location,
					9, 8,
					poseStack, buffer, -0.0356, 0.3545
				)
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
				helper.drawTextOnScreen(
					"CASEOH: Not Available",
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
				poseStack.mulPose(Axis.XN.rotationDegrees(Companion.coilRotation))
			}
			if (helper.shouldRenderCoil) helper.itemRenderer.renderItemModel(
				Companion.coilModel,
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
		Companion.helper.itemRenderer.renderItemModel(
			Companion.mainModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight
		)
		if (coilSpin) poseStack.mulPose(Axis.XN.rotationDegrees(Companion.coilRotation))
		Companion.helper.itemRenderer.renderItemModel(
			Companion.coilModel,
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
			Companion.helper
		)
	}
}