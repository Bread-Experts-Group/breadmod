package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.registry.component.ModDataComponents

abstract class AbstractToolGunModeRenderer(val id: ResourceLocation) : IToolGunMode.Renderer {
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

	inline fun <reified T : IToolGunMode> getToolGunMode(stack: ItemStack): T =
		stack.get(ModDataComponents.TOOL_GUN_DATA) as T

	final override fun getModeWidget(): ModeWidget = this.buildModeWidget().id(this.id).build()

	abstract fun buildModeWidget(): ModeWidget.Builder

	override fun getScreenTexture(): ResourceLocation = ModTextureLocations.SCREEN.location

	override fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true
}