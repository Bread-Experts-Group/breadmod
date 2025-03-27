package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder

@ToolGunMode
class ColorMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		println("todo...")
	}

	override fun getDisplayName(): Component = Component.literal("Color Mode")

	override fun getTooltip(): Component = Component.literal("TBD...").withStyle(ChatFormatting.UNDERLINE)

	override fun getUid(): ResourceLocation = this.toolGunLocation("color_mode")

	override fun getCustomRenderer(): Renderer = ColorRenderer(this.getUid())

	class ColorRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder =
			ModeWidget.Builder()
				.name("Color Mode")
				.icon(Items.BLUE_WOOL)
				.description("TBD...")
	}
}