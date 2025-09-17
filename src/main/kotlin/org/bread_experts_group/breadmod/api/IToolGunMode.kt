package org.bread_experts_group.breadmod.api

import com.mojang.serialization.Codec
import net.minecraft.client.KeyMapping
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.Key
import net.neoforged.neoforge.client.event.InputEvent.MouseButton
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.tool_gun.gui.ToolGunOverlay
import org.bread_experts_group.breadmod.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.fromClass
import org.bread_experts_group.breadmod.util.toClass

interface IToolGunMode {
	companion object {
		val CODEC: Codec<IToolGunMode> = Codec.STRING.xmap(::toClass, ::fromClass)

		/**
		 * Encodes this [IToolGunMode]'s qualifying name to a string and decodes back into a class.
		 */
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
	 * Fired when the tool gun's use function is called, fired before [action].
	 * if the returned value is false, cancel the main tool gun action.
	 */
	fun actionPre(level: Level, player: Player, usedHand: InteractionHand): Boolean = true

	/**
	 * Fired when the tool gun's use function is called, fired after [action].
	 */
	fun actionPost(level: Level, player: Player, usedHand: InteractionHand) {}

	/**
	 * Event bridge for [MouseScrollingEvent], used for handling mouse scrolling while holding the tool gun.
	 * Return true to cancel this event.
	 */
	fun mouseScrollAction(event: MouseScrollingEvent, stack: ItemStack, player: Player): Boolean = false

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
	 * Used for registering custom key inputs to this [IToolGunMode].
	 *
	 * Serves as an event bridge for [InputEvent.Key], handles keyboard inputs for this [IToolGunMode].
	 *
	 * - [KeyData] is passed into the [ToolGunOverlay] to display info about the specified key.
	 * - Any extra data that's set here is automatically synced to the server.
	 */
	fun registerKeys(into: MutableMap<Int, KeyData>) {}

	/** @return true if [event] action is equal to 1. */
	fun isKeyboardPress(event: Key): Boolean = event.action == 1
	fun keyMatchesInput(key: KeyMapping, event: Key): Boolean = event.key == key.key.value

	/**
	 * @return the id of this mode after the last slash as a string.
	 *
	 * * Used as a default impl for [getDisplayName] and the default mode widget name.
	 */
	fun unformattedName(): String = this.getUid().path.substringAfter("/")

	/**
	 * Used in the [ToolGunOverlay] and [ToolGunItemRenderer] for displaying this [IToolGunMode]'s name.
	 */
	fun getDisplayName(): Component = Component.literal(this.unformattedName())

	/**
	 * Used in the [ToolGunOverlay] for displaying this [IToolGunMode]'s tooltip.
	 */
	fun getTooltip(): Component = Component.literal("Override getTooltip to change this text!")

	/**
	 * The unique ID of this [IToolGunMode].
	 */
	fun getUid(): ResourceLocation

	fun getModeName(): String = this.getUid().path.substringAfter('/')

	/**
	 * Used to save define extra saved data in this [IToolGunMode].
	 *
	 * Fired when the tool gun is changing modes, or when saved data is being populated on a fresh tool gun.
	 */
	fun saveExtraData(tag: CompoundTag, level: Level) {}

	/**
	 * Used to load extra saved data upon instantiating or syncing this [IToolGunMode].
	 */
	fun loadExtraData(tag: CompoundTag, level: Level) {}

	fun shouldPlayToolGunSound(stack: ItemStack, player: Player): Boolean = true

	fun playToolGunSound(player: Player): Unit = player.playSound(ModSounds.TOOL_GUN.get(), 0.8f, 1f)

	/**
	 * Returns the custom renderer for this [IToolGunMode].
	 *
	 * Defaults to [EmptyMode]'s Renderer.
	 *
	 * * This shouldn't be overridden.
	 */
	fun getCustomRenderer(): IToolGunModeRenderer =
		Registry.toolGunRendererCache.getOrPut(this.getUid(), this::defineCustomRenderer)

	/**
	 * Defines the custom renderer for this [IToolGunMode].
	 */
	fun defineCustomRenderer(): IToolGunModeRenderer = EmptyMode.EmptyModeRenderer(this)

	/**
	 * Convenience function for setting up tool gun ids.
	 */
	fun toolGunLocation(modeName: String): ResourceLocation = modLocation(modeName)
}