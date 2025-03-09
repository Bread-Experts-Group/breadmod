package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		println("todo...")
		// todo block and entity raycast (look at 1.20 branch for examples)
	}

	override fun getDisplayName(): Component = Component.literal("Remover")

	override fun getTooltip(): Component = Component.literal("Remove Entities and Blocks.")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "remover_mode")

	override fun getCustomRenderer(): Renderer = RemoverRenderer(this.getUid())

	class RemoverRenderer(private val id: ResourceLocation) : AbstractToolGunModeRenderer() {
		override fun getModeWidget(): ModeWidget = ModeWidget.Builder()
			.icon(Items.STRUCTURE_VOID.defaultInstance)
			.description("Mode for removing entities and blocks from the world... humanely of course.")
			.name("Remover Mode")
			.id(this.id)
			.build()
	}
}