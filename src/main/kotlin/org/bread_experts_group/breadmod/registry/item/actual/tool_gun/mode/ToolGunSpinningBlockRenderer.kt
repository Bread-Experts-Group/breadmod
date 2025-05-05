package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderBlockModel
import org.bread_experts_group.breadmod.client.render.scaleFlat

class ToolGunSpinningBlockRenderer(
	id: ResourceLocation,
	val block: Block,
	builder: Builder
) : AbstractToolGunModeRenderer(id) {
	private val builder: Builder = builder.icon(this.block.asItem())
	override fun buildModeWidget(): Builder = this.builder

	override fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val millis = Util.getMillis()
		poseStack.pushPose()
		poseStack.translate(1.1, 0.06, -0.05)
		poseStack.scaleFlat(0.08f)
		poseStack.translate(0.5, 0.5, 0.5)
		poseStack.mulPose(Axis.YP.rotationDegrees((millis.toFloat() / 50f) % 360f))
		poseStack.translate(-0.5, -0.5, -0.5)
		localClient.blockRenderer.modelRenderer.renderBlockModel(
			poseStack.last(),
			buffer,
			this.block.defaultBlockState(),
			packedLight,
			packedOverlay
		)
		poseStack.popPose()
	}
}