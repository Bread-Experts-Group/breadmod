package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidget

@ToolGunMode
@Suppress("unused")
class ExplodeMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		level.explode(player, player.x, player.y, player.z, 20f, Level.ExplosionInteraction.MOB)
	}

	override fun getModeWidget(): ModeWidget = ModeWidget.Builder()
		.icon(Items.TNT.defaultInstance)
		.previewImage(ModTextureLocations.EXPLODE_PREVIEW)
		.name(modTranslatable("tool_gun", "explode", "mode", "name"))
		.description(modTranslatable("tool_gun", "explode", "mode", "description"))
		.id(this.getUid())
		.build()

	override fun getDisplayName(): Component = Component.literal("explode")

	override fun getTooltip(): Component = Component.literal("tooltip")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "explode_mode")

	override fun equals(other: Any?): Boolean =
		if (other is IToolGunMode) other.getUid() == this.getUid() else false

	override fun hashCode(): Int = this.getUid().hashCode()
}