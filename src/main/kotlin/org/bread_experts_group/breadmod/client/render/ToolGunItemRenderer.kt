package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.client.render.LerpTicker.LerpParams
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.formatMetric
import java.awt.Color
import java.lang.Math.clamp
import java.math.BigDecimal
import java.security.SecureRandom

// todo render BEWLRs in items/blockitems if they're rendered onto the tool gun
class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
), LerpTicker.BEWLR {
	private val caseOhInstrument: SecureRandom = SecureRandom()
	private var caseOhSize: BigDecimal = BigDecimal.TWO
	override val lerpParams: Array<LerpParams> = arrayOf(
		LerpParams(amount = -0.075f), // Delta
		LerpParams(), // Rotation
		LerpParams() // Recoil
	)

	/**
	 * Overrides tool gun rendering if this value isn't null.
	 */
	var rendererOverride: IToolGunModeRenderer? = null

	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta() {
		this.setParamPosition(0, 1.5f + localClient.timer.realtimeDeltaTicks)
		this.setParamPosition(2, 0.15f)
	}

	// todo maybe look into doing this more cleanly, this just seems a bit hacky imo
	override fun tick() {
		val delta = this.getRawValue(0)
		val recoil = this.getRawValue(2)
		if (!localClient.gamePaused()) {
			if (delta > 0f) this.tickPositionIndex(0)
			if (delta > 0f) this.tickPositionIndex(1, 40 * delta)
			if (recoil > 0f) this.tickPositionIndex(2, -0.0125f * delta)
		}
	}

	// Models
	@Suppress("unused")
	private val altModel: BakedModel =
		localClient.getModel("item/$TOOL_GUN_DEF/alt/tool_gun_alt")
	private val mainModel: BakedModel =
		localClient.getModel("item/$TOOL_GUN_DEF/item")
	private val coilModel: BakedModel =
		localClient.getModel("item/$TOOL_GUN_DEF/coil")

	fun renderToolGun(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		currentMode: IToolGunMode,
		overrideRenderType: Boolean = false,
		renderTypeOverride: RenderType = RenderType.solid()
	) {
		val delta = this.getRawValue(0)
		val rotation = if (delta > 0f) this.getLerpedValue(1) else this.getRawValue(1)
		val rawRecoil = this.getRawValue(2)
		val recoil = if (rawRecoil <= 0f) this.getRawValue(2) else this.getLerpedValue(2)

		if (displayContext.firstPerson()) {
			val modeRenderer: IToolGunModeRenderer = this.rendererOverride ?: currentMode.getCustomRenderer()
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (modeRenderer.shouldRecoil(stack, displayContext, currentMode)) poseStack.translate(
				-clamp(recoil, 0f, 1f),
				0f,
				0f
			)

			poseStack.pushPose()
			modeRenderer.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
			// Render Body Stage
			if (modeRenderer.shouldRenderMainBody(
					stack,
					displayContext,
					currentMode
				)
			) localClient.itemRenderer.renderItemModel(
				this.mainModel,
				stack,
				displayContext,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				overrideRenderType = overrideRenderType,
				renderTypeOverride = renderTypeOverride
			)
			modeRenderer.renderBodyStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
			poseStack.popPose()
			// Render Screen Stage
			poseStack.pushPose()
			if (modeRenderer.shouldRenderScreenContents(stack, displayContext, currentMode)) {
				modeRenderer.renderScreenBackground(modeRenderer.getScreenTexture(), 9, 8, poseStack, buffer)
				modeRenderer.drawTextOnScreen(
					currentMode.getDisplayName(),
					Color.RED.rgb, 0,
					false,
					localClient.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.414
				)
				this.caseOhSize =
					this.caseOhSize.add(this.caseOhInstrument.nextDouble(0.0, 1234511121314.0).toBigDecimal())
				modeRenderer.drawTextOnScreen(
					"CASEOH: ${this.caseOhSize.toDouble().formatMetric()}g",
					Color.RED.rgb,
					0,
					false,
					localClient.font,
					poseStack,
					buffer,
					posX = -0.035,
					posY = 0.361
				)
			}
			modeRenderer.renderScreenStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
			poseStack.popPose()
			// Render Coil Stage
			poseStack.pushPose()
			if (modeRenderer.shouldCoilSpin(stack, displayContext)) {
				poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
			}
			if (modeRenderer.shouldRenderCoil(
					stack,
					displayContext,
					currentMode
				)
			) localClient.itemRenderer.renderItemModel(
				this.coilModel,
				stack,
				displayContext,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				overrideRenderType = overrideRenderType,
				renderTypeOverride = renderTypeOverride
			)
			modeRenderer.renderCoilStage(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
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
		coilSpin: Boolean,
		overrideRenderType: Boolean = false,
		renderTypeOverride: RenderType = RenderType.solid()
	) {
		val rotation = this.getLerpedValue(1)
		localClient.itemRenderer.renderItemModel(
			this.mainModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight,
			overrideRenderType = overrideRenderType,
			renderTypeOverride = renderTypeOverride
		)
		if (coilSpin) poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
		localClient.itemRenderer.renderItemModel(
			this.coilModel,
			stack,
			displayContext,
			poseStack,
			buffer,
			packedOverlay,
			packedLight,
			overrideRenderType = overrideRenderType,
			renderTypeOverride = renderTypeOverride
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
		val currentMode = stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, ToolGunData.EMPTY).getMode()
		this.renderToolGun(
			stack,
			displayContext,
			poseStack,
			buffer,
			packedLight,
			packedOverlay,
			currentMode
		)
	}
}