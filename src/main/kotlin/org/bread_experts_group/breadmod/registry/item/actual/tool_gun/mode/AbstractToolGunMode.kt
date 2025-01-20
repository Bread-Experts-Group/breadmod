package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent.Key
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import org.bread_experts_group.breadmod.api.IToolGunMode

/**
 * Base abstract implementation of [IToolGunMode], use this class for your own modes.
 */
abstract class AbstractToolGunMode : IToolGunMode {
	abstract override fun action(level: Level, player: Player, stack: ItemStack)

	abstract override fun getDisplayName(): Component

	abstract override fun getTooltip(): Component

	abstract override fun getUid(): ResourceLocation

	override fun getCustomRenderer(): IToolGunMode.Renderer = EmptyMode.EmptyModeRenderer(this.getUid())

	override fun mouseScrollAction(event: MouseScrollingEvent, stack: ItemStack, player: Player): Boolean = false

	override fun mouseButtonPostAction(event: Post, stack: ItemStack, player: Player) {}

	override fun mouseButtonPreAction(event: Pre, stack: ItemStack, player: Player) {}

	override fun keyboardInputAction(event: Key, stack: ItemStack, player: Player) {}

	override fun equals(other: Any?): Boolean =
		if (other is IToolGunMode) other.getUid() == this.getUid() else false

	override fun hashCode(): Int = this.getUid().hashCode()
}