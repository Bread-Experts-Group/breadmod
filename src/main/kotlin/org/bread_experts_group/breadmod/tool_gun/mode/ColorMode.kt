package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder

@ToolGunMode
@Suppress("unused")
class ColorMode : IToolGunMode {
	companion object {
		@DataGenerateLanguage("en_us", "TBD...")
		val description: MutableComponent = modTranslatable("tool_gun", "color", "mode", "description")

		@DataGenerateLanguage("en_us", "Color Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "color", "mode", "name")

		@DataGenerateLanguage("en_us", "Color")
		val displayName: MutableComponent = modTranslatable("tool_gun", "color", "mode", "display_name")

		@DataGenerateLanguage("en_us", "TBD...")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "color", "mode", "tooltip")
	}

	override fun action(level: Level, player: Player, stack: ItemStack) {
		// todo action stuff
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip.withStyle(ChatFormatting.UNDERLINE)
	override fun getUid(): ResourceLocation = this.toolGunLocation("color_mode")
	override fun getCustomRenderer(): IToolGunModeRenderer = ColorRenderer(this)

	class ColorRenderer(private val mode: IToolGunMode) : IToolGunModeRenderer {
		override fun buildModeWidget(): Builder =
			ModeWidget.Builder()
				.name(Companion.name)
				.icon(Items.BLUE_WOOL)
				.description(Companion.description)

		override fun render(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int
		) {
			super.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
		}

		override fun getMode(): IToolGunMode = this.mode
	}
}