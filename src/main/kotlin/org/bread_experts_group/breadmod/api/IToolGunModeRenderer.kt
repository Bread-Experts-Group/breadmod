package org.bread_experts_group.breadmod.api

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.ToolGunOverlay
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.util.Vector3fAxisX
import org.bread_experts_group.breadmod.util.Vector3fZero
import org.joml.Vector3f
import java.awt.Color

interface IToolGunModeRenderer {
	companion object {
		/**
		 * Default screen brightness.
		 */
		const val SCREEN_TINT: Int = LightTexture.FULL_BRIGHT
		const val SCREEN_TEXT_X: Double = -0.0434
		const val SCREEN_TEXT_Y: Double = 0.4215
		const val SCREEN_TEXT_Z: Double = 0.8317
	}

	val font: Font
		get() = localClient.font

	fun shouldRecoil(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		currentMode: IToolGunMode
	): Boolean = true

	fun shouldRenderCoil(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		currentMode: IToolGunMode
	): Boolean = true

	fun shouldRenderMainBody(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		currentMode: IToolGunMode
	): Boolean = true

	fun shouldRenderScreenContents(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		currentMode: IToolGunMode
	): Boolean = true

	fun initialScreenTranslations(
		poseStack: PoseStack,
		posX: Double,
		posY: Double,
		posZ: Double,
		scale: Float
	) {
		poseStack.pushPose()
		poseStack.mulPose(Axis.XP.rotationDegrees(90f))
		poseStack.mulPose(Axis.YP.rotationDegrees(90f))
		poseStack.mulPose(Axis.ZP.rotationDegrees(90f))
		poseStack.mulPose(Axis.XN.rotationDegrees(22.5f))
		poseStack.translate(posX, -posY, posZ)
		poseStack.scaleFlat(scale)
	}

	/**
	 * Renders the background for the screen. Supplied [texture] must be scaled in multiples of 9x8.
	 */
	fun renderScreenBackground(
		texture: ResourceLocation,
		textureWidth: Int,
		textureHeight: Int,
		poseStack: PoseStack,
		buffer: MultiBufferSource
	) {
		this.initialScreenTranslations(poseStack, -0.0357, 0.3545, 0.8319, 0.07f)
		poseStack.scale(1.025f, 0.856f, 1f)
		drawQuad(
			poseStack,
			buffer,
			RenderType.text(texture),
			Color.WHITE.rgb,
			Vector3fAxisX,
			Vector3fZero,
			Vector3f(1f, -1f, 0f),
			Vector3f(0f, -1f, 0f),
			textureWidth.toFloat(),
			textureWidth.toFloat(),
			textureHeight.toFloat(),
			textureHeight.toFloat()
		)
		poseStack.popPose()
	}

	fun drawTextOnScreen(
		component: Component,
		color: Int,
		backgroundColor: Int,
		dropShadow: Boolean,
		fontRenderer: Font,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		posX: Double = Companion.SCREEN_TEXT_X,
		posY: Double = Companion.SCREEN_TEXT_Y,
		posZ: Double = Companion.SCREEN_TEXT_Z,
		scale: Float = 0.0007f
	) {
		this.initialScreenTranslations(poseStack, posX, posY, posZ, scale)
		fontRenderer.renderText(
			component.visualOrderText, color, backgroundColor, poseStack,
			buffer, dropShadow,
			Companion.SCREEN_TINT
		)
		poseStack.popPose()
	}

	/**
	 * @see drawTextOnScreen
	 */
	fun drawTextOnScreen(
		text: String,
		color: Int,
		backgroundColor: Int,
		dropShadow: Boolean,
		fontRenderer: Font,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		posX: Double = Companion.SCREEN_TEXT_X,
		posY: Double = Companion.SCREEN_TEXT_Y,
		posZ: Double = Companion.SCREEN_TEXT_Z,
		scale: Float = 0.0007f
	): Unit = this.drawTextOnScreen(
		Component.literal(text),
		color, backgroundColor, dropShadow, fontRenderer, poseStack, buffer, posX, posY, posZ, scale
	)

	/**
	 * Used to render special effects and/or models on the tool gun's
	 * [net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer].
	 * Fires before the other render stages.
	 * @see renderScreenStage
	 * @see renderCoilStage
	 * @see renderBodyStage
	 */
	fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * Used to render text and/or icons positioned to the tool gun screen.
	 * Fires after all other stages have rendered.
	 * @see render
	 */
	fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * Used to render effects and/or models to the tool gun's coil.
	 * Fires after [render] and [renderBodyStage].
	 * This rotates along with the coil.
	 * @see render
	 * @see renderBodyStage
	 */
	fun renderCoilStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * Used to render effects and/or models to the tool gun's main body.
	 * Fires after [render].
	 * @see render
	 */
	fun renderBodyStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * @return the [IToolGunMode] for this [IToolGunModeRenderer].
	 */
	fun getMode(): IToolGunMode

	/**
	 * Override this to build a mode widget with its id already prefixed.
	 */
	fun buildModeWidget(): ModeWidget.Builder = ModeWidget.builder()
		.name(this.getMode().getDisplayName())
		.description("[Description here]")

	/**
	 * Gets the mode widget for this [IToolGunModeRenderer].
	 *
	 * * Override [buildModeWidget] to build a widget with the id already prefixed.
	 */
	fun getModeWidget(): ModeWidget = this.buildModeWidget().id(this.getMode().getUid()).build()

	fun getScreenTexture(): ResourceLocation = ModGuiElements.SCREEN.location

	fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true

	/**
	 * Used for rendering additional elements onto the [ToolGunOverlay].
	 */
	fun renderOverlayAdditions(
		guiGraphics: GuiGraphics,
		originX: Int,
		originY: Int,
		deltaTracker: DeltaTracker,
		stack: ItemStack,
		data: ToolGunData
	) {
	}

	/**
	 * Hook for rendering into the level.
	 */
	fun renderLevelStageEvent(
		event: RenderLevelStageEvent,
		bufferSource: MultiBufferSource,
		player: LocalPlayer,
		data: ToolGunData
	) {
	}
}