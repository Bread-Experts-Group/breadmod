package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget

abstract class AbstractToolGunModeRenderer(val id: ResourceLocation) : IToolGunModeRenderer {
	override fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	override fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	override fun renderCoilStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	override fun renderBodyStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	final override fun getModeWidget(): ModeWidget = this.buildModeWidget().id(this.id).build()

	abstract fun buildModeWidget(): ModeWidget.Builder

	override fun getScreenTexture(): ResourceLocation = ModTextureLocations.SCREEN.location

	override fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true
}