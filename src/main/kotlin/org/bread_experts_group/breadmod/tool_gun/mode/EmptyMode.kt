package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder

class EmptyMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
	}

	override fun getDisplayName(): Component = Component.literal("???")
	override fun getUid(): ResourceLocation = this.toolGunLocation("empty_mode")
	override fun getCustomRenderer(): IToolGunModeRenderer = EmptyModeRenderer(this.getUid())

	@DataGenerateLanguage("en_us", "If you see this mode then something probably went wrong!")
	override fun getTooltip(): Component = modTranslatable("tool_gun", "empty", "mode", "tooltip")

	class EmptyModeRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder = ModeWidget.Builder()
			.name(this.id.path.substringAfter("/"))
			.description("<missing mode>")
	}
}