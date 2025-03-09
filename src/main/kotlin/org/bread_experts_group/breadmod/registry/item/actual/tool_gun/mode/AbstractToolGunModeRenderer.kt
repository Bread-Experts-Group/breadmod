package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.render.ToolGunRenderHelper

abstract class AbstractToolGunModeRenderer : IToolGunMode.Renderer {
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

	override fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true
}