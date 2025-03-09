package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder

class EmptyMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
	}

	override fun getDisplayName(): Component = Component.literal("???")
	override fun getUid(): ResourceLocation = modLocation("tool_gun", "empty_mode")
	override fun getCustomRenderer(): IToolGunMode.Renderer = EmptyModeRenderer(this.getUid())
	override fun getTooltip(): Component =
		Component.literal("If you see this mode then something probably went wrong!")

	class EmptyModeRenderer(val id: ResourceLocation) : AbstractToolGunModeRenderer() {
		override fun getModeWidget(): ModeWidget =
			Builder()
				.id(this.id)
				.name(this.id.path.substringAfter("/"))
				.description("<missing mode>")
				.build()
	}
}