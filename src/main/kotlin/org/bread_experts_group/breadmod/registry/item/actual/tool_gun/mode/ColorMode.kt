package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

@ToolGunMode
@Suppress("unused")
class ColorMode : AbstractToolGunMode() {
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
		println("todo...")
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip.withStyle(ChatFormatting.UNDERLINE)
	override fun getUid(): ResourceLocation = this.toolGunLocation("color_mode")
	override fun getCustomRenderer(): Renderer = ColorRenderer(this.getUid())

	class ColorRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder =
			ModeWidget.Builder()
				.name(Companion.name)
				.icon(Items.BLUE_WOOL)
				.description(Companion.description)
	}
}