package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.util.formatNumberBigDecimal
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import java.awt.Color
import java.lang.Math.clamp
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.SecureRandom

// todo render BEWLRs in items/blockitems if they're rendered onto the tool gun
object ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	private val deltaTracker: DeltaTracker = localClient.timer
	private val partialTick: Float = this.deltaTracker.gameTimeDeltaTicks
	private val caseOhInstrument: SecureRandom = SecureRandom()
	private var caseOhSize: BigDecimal = BigDecimal.TWO
	var delta: Float = 0f
	var rotation: Float = 0f
	var recoil: Float = 0f

	/**
	 * Overrides tool gun rendering if this value isn't null.
	 */
	var rendererOverride: IToolGunModeRenderer? = null

	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta(hashcode: Int) {
		this.delta = 1f
		this.recoil = 0.1f
	}

	// Models
	@Suppress("unused")
	private val altModel: BakedModel =
		localClient.modelManager.getModel("item/$TOOL_GUN_DEF/alt/tool_gun_alt")
	private val mainModel: BakedModel =
		localClient.modelManager.getModel("item/$TOOL_GUN_DEF/item")
	private val coilModel: BakedModel =
		localClient.modelManager.getModel("item/$TOOL_GUN_DEF/coil")

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
		val playerItemHash = getStackInPlayerHand(localClient.player).hashCode()
		val stackHash = stack.hashCode()
		if (playerItemHash == stackHash && !localClient.gamePaused()) {
			if (this.delta > 0f) this.delta -= 0.025f * this.partialTick
			if (this.delta > 0f) this.rotation += (40 * this.delta) * this.partialTick
			if (this.recoil > 0f) this.recoil -= 0.025f * this.partialTick * this.delta / 3.5f
		}

		if (displayContext.firstPerson()) {
			val modeRenderer: IToolGunModeRenderer = this.rendererOverride ?: currentMode.getCustomRenderer()
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (modeRenderer.shouldRecoil(stack, displayContext, currentMode)) poseStack.translate(
				-clamp(this.recoil, 0f, 1f),
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
				val (truncated, unit) = formatNumberBigDecimal(this.caseOhSize)
				modeRenderer.drawTextOnScreen(
					"CASEOH: ${truncated.setScale(2, RoundingMode.DOWN)} ${unit}g",
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
				poseStack.mulPose(Axis.XN.rotationDegrees(this.rotation))
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
		if (coilSpin) poseStack.mulPose(Axis.XN.rotationDegrees(this.rotation))
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
		val (currentMode, _, _) = stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, ToolGunData.EMPTY)
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