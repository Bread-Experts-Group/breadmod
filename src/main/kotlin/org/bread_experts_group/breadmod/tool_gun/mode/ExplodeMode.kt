package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.util.BreadModExplosion
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class ExplodeMode : IToolGunMode {
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
			val targetBlock = player.rayCast(500.0, blocks()) ?: return
			BreadModExplosion
				.calculate(level, targetBlock.position.subtract(targetBlock.castDirection), 20f, 1000)
				.explode(player)
		}
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun getUid(): ResourceLocation = this.toolGunLocation("explode_mode")
	override fun defineCustomRenderer(): IToolGunModeRenderer = ToolGunSpinningBlockRenderer(
		this,
		Blocks.TNT,
		Builder()
			.previewImage(ModGuiElements.EXPLODE_PREVIEW)
			.name(Companion.name)
			.description(Companion.description)
	)
}