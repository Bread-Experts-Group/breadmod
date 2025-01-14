package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.Codec
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseButton
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import kotlin.reflect.full.primaryConstructor

abstract class ToolGunMode {
	companion object {
		val CODEC: Codec<ToolGunMode> = Codec.STRING.xmap(this::convertFromString, this::convertToString)
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunMode> =
			object : StreamCodec<FriendlyByteBuf, ToolGunMode> {
				override fun decode(buffer: FriendlyByteBuf): ToolGunMode =
					this@Companion.convertFromString(buffer.readUtf())

				override fun encode(buffer: FriendlyByteBuf, value: ToolGunMode) {
					buffer.writeUtf(this@Companion.convertToString(value))
				}
			}

		private fun convertToString(clazz: ToolGunMode): String = clazz::class.qualifiedName!!
		private fun convertFromString(path: String): ToolGunMode =
			Class.forName(
				path,
				true,
				ToolGunMode::class.java.classLoader
			).kotlin.primaryConstructor?.call() as ToolGunMode
	}

	/**
	 * Main action method for this [ToolGunMode].
	 * Triggered using right click.
	 */
	abstract fun action(level: Level, player: Player, stack: ItemStack)

	/**
	 * Event bridge for [MouseScrollingEvent], used for handling mouse scrolling while holding the tool gun.
	 * Return true to cancel this event.
	 */
	fun mouseScrollAction(event: MouseScrollingEvent, stack: ItemStack, player: Player): Boolean = true

	/**
	 * Event bridge for [MouseButton.Post], used for handling mouse button presses.
	 * Fired after vanilla mouse button processing.
	 * @see mouseButtonPreAction
	 */
	fun mouseButtonPostAction(event: MouseButton.Post, stack: ItemStack, player: Player) {}

	/**
	 * Event bridge for [MouseButton.Pre], used for handling mouse button presses.
	 * Fired before vanilla mouse button processing.
	 * @see mouseButtonPostAction
	 */
	fun mouseButtonPreAction(event: MouseButton.Pre, stack: ItemStack, player: Player) {}

	/**
	 * Event bridge for [InputEvent.Key], used for handling keyboard inputs while holding the tool gun.
	 */
	fun keyboardInputAction(event: InputEvent.Key, stack: ItemStack, player: Player) {}

	/**
	 * Used to render special effects and/or models on the tool gun's [BlockEntityWithoutLevelRenderer].
	 */
	fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
	}
}