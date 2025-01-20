package org.bread_experts_group.breadmod.api

import com.mojang.serialization.Codec
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseButton
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import org.bread_experts_group.breadmod.util.fromClass
import org.bread_experts_group.breadmod.util.toClass

interface IToolGunMode {
	companion object {
		val CODEC: Codec<IToolGunMode> = Codec.STRING.xmap(::toClass, ::fromClass)
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, IToolGunMode> =
			object : StreamCodec<FriendlyByteBuf, IToolGunMode> {
				override fun decode(buffer: FriendlyByteBuf): IToolGunMode = toClass(buffer.readUtf())

				override fun encode(buffer: FriendlyByteBuf, value: IToolGunMode) {
					buffer.writeUtf(fromClass(value))
				}
			}
	}

	/**
	 * Main action method for this [IToolGunMode].
	 * Triggered using right click.
	 */
	fun action(level: Level, player: Player, stack: ItemStack)

	/**
	 * Event bridge for [MouseScrollingEvent], used for handling mouse scrolling while holding the tool gun.
	 * Return true to cancel this event.
	 */
	fun mouseScrollAction(event: MouseScrollingEvent, stack: ItemStack, player: Player): Boolean

	/**
	 * Event bridge for [MouseButton.Post], used for handling mouse button presses.
	 * Fired after vanilla mouse button processing.
	 * @see mouseButtonPreAction
	 */
	fun mouseButtonPostAction(event: MouseButton.Post, stack: ItemStack, player: Player)

	/**
	 * Event bridge for [MouseButton.Pre], used for handling mouse button presses.
	 * Fired before vanilla mouse button processing.
	 * @see mouseButtonPostAction
	 */
	fun mouseButtonPreAction(event: MouseButton.Pre, stack: ItemStack, player: Player)

	/**
	 * Event bridge for [InputEvent.Key], used for handling keyboard inputs while holding the tool gun.
	 */
	fun keyboardInputAction(event: InputEvent.Key, stack: ItemStack, player: Player)

	fun getDisplayName(): Component

	fun getTooltip(): Component

	fun getUid(): ResourceLocation
}