package org.bread_experts_group.breadmod.client.render.tool_gun

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_LEFT_HAND
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.transparentColor
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import java.awt.Color

class ToolGunItemRenderer :
	BlockEntityWithoutLevelRenderer(localClient.blockEntityRenderDispatcher, localClient.entityModels) {
	private companion object {
		val useAltModel = ModConfiguration.CLIENT.useAlternateToolGunModel
		var context = ToolGunRenderContext.init()
		val animHandler = ToolGunAnimationHandler
		val mainModel: BakedModel =
			this.context.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/item"))
		val coilModel: BakedModel =
			this.context.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/coil"))
		val altModel: BakedModel =
			this.context.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt"))
	}

	override fun onResourceManagerReload(resourceManager: ResourceManager) {
		Companion.context = ToolGunRenderContext.init()
	}

	private fun renderToolGun(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		currentMode: IToolGunMode,
		context: ToolGunRenderContext
	) {
		val rotation = Companion.animHandler.coilRotation
		val recoil = Companion.animHandler.recoil
		val isLeftHand = displayContext == FIRST_PERSON_LEFT_HAND

		if (displayContext.firstPerson()) {
			poseStack.pushPose()
			// Main recoil translations
			if (context.shouldRecoil) poseStack.translate(-recoil, 0f, 0f)

			poseStack.pushPose()
			currentMode.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, context)
			// Render Body Stage
			if (context.shouldRenderMainBody) context.itemRenderer.renderItemModel(
				Companion.mainModel,
				stack,
				displayContext,
				false,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				true
			)
			currentMode.renderBodyStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, context)
			poseStack.popPose()
			// Render Screen Stage
			poseStack.pushPose()
			if (context.shouldRenderScreenContents) {
				drawTextOnScreen(
					Component.literal("THE FUNNY"),
					Color.WHITE.rgb, transparentColor().rgb,
					false,
					context.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.414
				)
				drawTextOnScreen(
					"CASEOH: Not Available",
					Color.RED.rgb,
					transparentColor().rgb,
					false,
					context.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.361
				)
			}
			currentMode.renderScreenStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, context)
			poseStack.popPose()
			// Render Coil Stage
			poseStack.pushPose()
			if (currentMode.shouldCoilSpin(stack, displayContext)) poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
			if (context.shouldRenderCoil) context.itemRenderer.renderItemModel(
				Companion.coilModel,
				stack,
				displayContext,
				false,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				true
			)
			currentMode.renderCoilStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, context)
			poseStack.popPose()
			// End Render, pop the final push
			poseStack.popPose()
		} else {
			context.itemRenderer.renderItemModel(
				Companion.mainModel,
				stack,
				displayContext,
				false,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				true
			)
			poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
			context.itemRenderer.renderItemModel(
				Companion.coilModel,
				stack,
				displayContext,
				false,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				true
			)
		}

		Companion.animHandler.clientTick()
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
			Companion.context
		)
	}
}