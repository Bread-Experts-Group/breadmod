package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.renderBlockModel
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunRenderHelper

@ToolGunMode
@Suppress("unused")
class ExplodeMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		level.explode(player, player.x, player.y, player.z, 20f, Level.ExplosionInteraction.MOB)
	}

	override fun getDisplayName(): Component = Component.literal("explode")

	override fun getTooltip(): Component = Component.literal("tooltip")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "explode_mode")

	override fun getCustomRenderer(): IToolGunMode.Renderer = ExplodeModeRenderer(this.getUid())

	class ExplodeModeRenderer(val id: ResourceLocation) : AbstractToolGunModeRenderer() {
		override fun getModeWidget(): ModeWidget = ModeWidget.Builder()
			.icon(Items.TNT.defaultInstance)
			.previewImage(ModTextureLocations.EXPLODE_PREVIEW)
			.name(modTranslatable("tool_gun", "explode", "mode", "name"))
			.description(modTranslatable("tool_gun", "explode", "mode", "description"))
			.id(this.id)
			.build()

		override fun render(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int,
			helper: ToolGunRenderHelper
		) {
			val millis = Util.getMillis()
			poseStack.pushPose()
			poseStack.translate(1.1, 0.06, -0.05)
			poseStack.scaleFlat(0.08f)
			poseStack.translate(0.5, 0.0, 0.5)
			poseStack.mulPose(Axis.YP.rotationDegrees((millis.toFloat() / 50f) % 360f))
			poseStack.translate(-0.5, 0.0, -0.5)
			helper.blockModelRenderer.renderBlockModel(
				poseStack.last(),
				buffer,
				Blocks.TNT.defaultBlockState(),
				packedLight,
				packedOverlay
			)
			poseStack.popPose()
		}
	}
}