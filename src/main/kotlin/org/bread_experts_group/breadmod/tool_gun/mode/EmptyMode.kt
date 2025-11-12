package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder

object EmptyMode : IToolGunMode {
	override fun action(level: Level, player: Player, stack: ItemStack, usedHand: InteractionHand) {
	}

	override fun getDisplayName(): Component = Component.literal("???")
	override fun getUid(): ResourceLocation = this.toolGunLocation("empty_mode")
	override fun defineCustomRenderer(): IToolGunModeRenderer = EmptyModeRenderer(this)

	@DataGenerateLanguage("en_us", "If you see this mode then something probably went wrong!")
	override fun getTooltip(): Component = modTranslatable("tool_gun", "empty", "mode", "tooltip")

	class EmptyModeRenderer(private val mode: IToolGunMode) : IToolGunModeRenderer {
		override fun buildModeWidget(): Builder = ModeWidget.Builder()
			.name(this.mode.unformattedName())
			.description("<missing mode>")

		override fun getMode(): IToolGunMode = this.mode
	}
}