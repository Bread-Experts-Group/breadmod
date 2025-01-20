package org.bread_experts_group.breadmod.client.tool_gun.client_modes

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunModeClient
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunRenderHelper

abstract class AbstractToolGunModeClient : IToolGunModeClient {
	override fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	) {
	}

	override fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	) {
	}

	override fun renderCoilStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	) {
	}

	override fun renderBodyStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	) {
	}

	abstract override fun getModeWidget(): ModeWidget

	abstract override fun getDisplayName(): Component

	override fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true

	abstract override fun getUid(): ResourceLocation
}