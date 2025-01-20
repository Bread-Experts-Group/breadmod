package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.ToolGunMode

@ToolGunMode(Dist.DEDICATED_SERVER)
@Suppress("unused")
class ExplodeMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		level.explode(player, player.x, player.y, player.z, 20f, Level.ExplosionInteraction.MOB)
	}

	override fun getDisplayName(): Component = Component.literal("explode")

	override fun getTooltip(): Component = Component.literal("tooltip")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "explode_mode")
}