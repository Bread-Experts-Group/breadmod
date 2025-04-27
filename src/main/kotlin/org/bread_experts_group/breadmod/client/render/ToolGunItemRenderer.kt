package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemDisplayContext.GUI
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.util.formatNumberBigDecimal
import java.awt.Color
import java.lang.Math.clamp
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.SecureRandom

object ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
	localClient.blockEntityRenderDispatcher,
	localClient.entityModels
) {
	private val deltaTracker = localClient.timer
	private val partialTick = this.deltaTracker.gameTimeDeltaTicks
	private val caseOhInstrument: SecureRandom = SecureRandom()
	private var caseOhSize: BigDecimal = BigDecimal.TWO
	private val rotationMap: MutableMap<Int, Triple<Float, Float, Float>> = mutableMapOf()

	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta(hashcode: Int) {
		val data = this.rotationMap[hashcode] ?: return
		this.rotationMap[hashcode] = Triple(1f, data.second, 0.1f)
	}

	// Models
	@Suppress("unused")
	private val altModel: BakedModel =
		localClient.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt"))
	private val mainModel: BakedModel =
		localClient.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/item"))
	private val coilModel: BakedModel =
		localClient.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/coil"))

	override fun onResourceManagerReload(resourceManager: ResourceManager) {
		IToolGunModeRenderer.modelManager = localClient.modelManager
		IToolGunModeRenderer.itemRenderer = localClient.itemRenderer
		IToolGunModeRenderer.blockModelRenderer = localClient.blockRenderer.modelRenderer
		IToolGunModeRenderer.blockRenderDispatcher = localClient.blockRenderer
		IToolGunModeRenderer.entityRenderDispatcher = localClient.entityRenderDispatcher
		IToolGunModeRenderer.font = localClient.font
	}

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
		val playerItemHash = (localClient.player ?: return).mainHandItem.hashCode()
		val stackHash = stack.hashCode()
		if (this.rotationMap[stackHash] == null) this.rotationMap[stackHash] = Triple(0f, 0f, 0f)
		if (playerItemHash == stackHash && this.rotationMap[stackHash] != null) {
			this.rotationMap[stackHash]?.let { (delta, rotation, recoil) ->
				var newDelta = delta
				var newRot = rotation
				var newRecoil = recoil
				if (newDelta > 0f) {
					newDelta -= 0.025f * this.partialTick
					newRot += (40f * newDelta) * this.partialTick
					newRecoil -= 0.025f * this.partialTick * newDelta / 3.5f
				}
				this.rotationMap[stackHash] = Triple(newDelta, newRot, newRecoil)
			}
		}

		if (displayContext.firstPerson()) {
			val modeRenderer = currentMode.getCustomRenderer()
			poseStack.pushPose()
			// Main recoil translations
			// todo improve recoil
			if (modeRenderer.shouldRecoil(stack, displayContext, currentMode)) poseStack.translate(
				-clamp((this.rotationMap[stackHash] ?: return).third, 0f, 1f),
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
			) IToolGunModeRenderer.itemRenderer.renderItemModel(
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
					Color.RED.rgb, transparentColor().rgb,
					false,
					IToolGunModeRenderer.font,
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
					transparentColor().rgb,
					false,
					IToolGunModeRenderer.font,
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
				poseStack.mulPose(Axis.XN.rotationDegrees((this.rotationMap[stackHash] ?: return).second))
			}
			if (modeRenderer.shouldRenderCoil(
					stack,
					displayContext,
					currentMode
				)
			) IToolGunModeRenderer.itemRenderer.renderItemModel(
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
		val stackHash = stack.hashCode()
		IToolGunModeRenderer.itemRenderer.renderItemModel(
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
		if (coilSpin) poseStack.mulPose(Axis.XN.rotationDegrees((this.rotationMap[stackHash] ?: return).second))
		IToolGunModeRenderer.itemRenderer.renderItemModel(
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