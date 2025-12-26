package org.bread_experts_group.breadmod.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.RendererWithBEWLRLerpTicker
import org.bread_experts_group.breadmod.client.render.buffer.BeamBufferTask
import org.bread_experts_group.breadmod.client.render.gamePaused
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
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
), RendererWithBEWLRLerpTicker<ToolGunItemRenderer.LerpLabels> {
	private val caseOhInstrument: SecureRandom = SecureRandom()
	private var caseOhSize: BigDecimal = BigDecimal.TWO
	override val lerpTicker: LerpTicker.BEWLR<LerpLabels> = object : LerpTicker.BEWLR<LerpLabels>(
		LerpLabels.DELTA to LerpParams(incrementAmount = -0.075f, clampMin = 0f, clampMax = 2f),
		LerpLabels.ROTATION to LerpParams(isHandledManually = true),
		LerpLabels.RECOIL to LerpParams(isHandledManually = true, clampMin = 0f, clampMax = 0.15f)
	) {
		override fun tick() {
			if (!localClient.gamePaused()) {
				val delta = this.getRawValue(LerpLabels.DELTA)
				this.tickIndex(LerpLabels.DELTA)
				this.tickCustom(LerpLabels.ROTATION) { params ->
					if (delta > 0f) params.setClampedPos(40 * delta)
				}
				this.tickIndex(LerpLabels.RECOIL, -0.0125f * delta)
			}
		}
	}

	/**
	 * Overrides tool gun rendering if this value isn't null.
	 */
	var rendererOverride: IToolGunModeRenderer? = null

	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta() {
		this.lerpTicker.setParamPosition(LerpLabels.DELTA, 1.5f + localClient.timer.realtimeDeltaTicks)
		this.lerpTicker.setParamPosition(LerpLabels.RECOIL, 0.15f)
	}

	// Models
	@Suppress("unused")
	private val altModel: BakedModel = localClient.getModel("item/$TOOL_GUN_DEF/alt/tool_gun_alt")
	private val mainModel: BakedModel = localClient.getModel("item/$TOOL_GUN_DEF/item")
	private val coilModel: BakedModel = localClient.getModel("item/$TOOL_GUN_DEF/coil")

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
		val rotation = this.lerpTicker.getLerpedOrRawValue(LerpLabels.ROTATION, LerpLabels.DELTA)
		val recoil = this.lerpTicker.getLerpedOrRawValue(LerpLabels.RECOIL, LerpLabels.DELTA)

		if (displayContext.firstPerson()) {
			val modeRenderer: IToolGunModeRenderer = this.rendererOverride ?: currentMode.getCustomRenderer()
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (modeRenderer.shouldRecoil(stack, displayContext, currentMode))
				poseStack.translate(-clamp(recoil, 0f, 1f), 0f, 0f)

			poseStack.pushPose()
			modeRenderer.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
			// Render Body Stage
			if (modeRenderer.shouldRenderMainBody(
					stack,
					displayContext,
					currentMode
				)
			) {
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
				poseStack.pushPose()
				poseStack.translate(1.35f, 0f, 0f)
				// todo look into replicating the PoseStack and transforms for rendering the beam in BEWLR.
				//  It should allow for better control on where and when the beam renders when faced with different display contexts,
				//  instead of doing... this to render the beam.
				//  render process: renderByItem (BEWLR) <- render (ItemRenderer) <- renderStatic (ItemRenderer)
				//  <- renderItem (ItemInHandRenderer) <- renderArmWithItem (ItemInHandRenderer)
				//  <- renderHandsWithItems (ItemInHandRenderer) <- renderItemInHand (GameRenderer)
				if (BeamBufferTask.needsNewTask) {
					BeamBufferTask.create(poseStack.last().copy())
					BeamBufferTask.needsNewTask = false
				}
				poseStack.popPose()
			}
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
					poseStack,
					buffer,
					0.009,
					-0.008
				)
				this.caseOhSize =
					this.caseOhSize.add(this.caseOhInstrument.nextDouble(0.0, 1234511121314.0).toBigDecimal())
				modeRenderer.drawTextOnScreen(
					Component.literal("CASEOH: ${this.caseOhSize.toDouble().formatMetric()}g"),
					Color.RED.rgb,
					0,
					false,
					poseStack,
					buffer,
					0.009,
					-0.061
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
		val rotation = this.lerpTicker.getLerpedValue(LerpLabels.ROTATION)
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
		val data = ToolGunData.get(stack)
		this.renderToolGun(
			stack,
			displayContext,
			poseStack,
			buffer,
			packedLight,
			packedOverlay,
			data.getMode()
		)
	}

	enum class LerpLabels {
		DELTA,
		ROTATION,
		RECOIL
	}
}