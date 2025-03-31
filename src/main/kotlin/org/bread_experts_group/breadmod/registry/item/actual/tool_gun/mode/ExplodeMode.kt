package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.client.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.client.render.renderBlockModel
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.experimental.BreadModExplosion
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class ExplodeMode : AbstractToolGunMode() {
	companion object {
		@DataGenerateLanguage("en_us", "Explode Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "explode", "mode", "name")

		@DataGenerateLanguage("en_us", "goes kaboom and probably blows a fuse in your house or two")
		val description: MutableComponent = modTranslatable("tool_gun", "explode", "mode", "description")

		@DataGenerateLanguage("en_us", "Explode")
		val displayName: MutableComponent = modTranslatable("tool_gun", "explode", "mode", "display_name")

		@DataGenerateLanguage("en_us", "Kaboom")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "explode", "mode", "tooltip")
	}

	override fun action(level: Level, player: Player, stack: ItemStack) {
		if (!level.isClientSide) {
			val targetBlock = player.rayCast(500, blocks(Blocks.AIR)) ?: return
			BreadModExplosion
				.calculate(level, targetBlock.position.subtract(targetBlock.direction), 20f)
				.explode(player)
		}
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun getUid(): ResourceLocation = this.toolGunLocation("explode_mode")
	override fun getCustomRenderer(): IToolGunMode.Renderer = ExplodeModeRenderer(this.getUid())

	class ExplodeModeRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder = ModeWidget.Builder()
			.icon(Items.TNT)
			.previewImage(ModTextureLocations.EXPLODE_PREVIEW)
			.name(Companion.name)
			.description(Companion.description)

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