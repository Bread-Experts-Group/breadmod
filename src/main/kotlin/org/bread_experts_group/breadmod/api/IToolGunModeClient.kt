package org.bread_experts_group.breadmod.api

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunRenderHelper

interface IToolGunModeClient {
	/**
	 * Used to render special effects and/or models on the tool gun's [BlockEntityWithoutLevelRenderer].
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
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render text and/or icons positioned to the tool gun screen, fires after everything else.
	 * @see render
	 */
	fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render effects and/or models to the tool gun's coil, fires after [render] and [renderBodyStage].
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
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render effects and/or models to the tool gun's main body, fires after [render].
	 * @see render
	 */
	fun renderBodyStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	fun getModeWidget(): ModeWidget

	fun getDisplayName(): Component

	fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean

	/**
	 * This MUST be the same uid as in your tool gun mode.
	 */
	fun getUid(): ResourceLocation
}