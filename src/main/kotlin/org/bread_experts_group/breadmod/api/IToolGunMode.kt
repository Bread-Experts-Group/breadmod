package org.bread_experts_group.breadmod.api

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.Codec
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseButton
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget
import kotlin.reflect.full.primaryConstructor

interface IToolGunMode {
	companion object {
		val CODEC: Codec<IToolGunMode> = Codec.STRING.xmap(this::toClass, this::fromClass)
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, IToolGunMode> =
			object : StreamCodec<FriendlyByteBuf, IToolGunMode> {
				override fun decode(buffer: FriendlyByteBuf): IToolGunMode =
					this@Companion.toClass(buffer.readUtf())

				override fun encode(buffer: FriendlyByteBuf, value: IToolGunMode) {
					buffer.writeUtf(this@Companion.fromClass(value))
				}
			}

		inline fun <reified T> toClass(path: String): T =
			Class.forName(
				path,
				true,
				T::class.java.classLoader
			).kotlin.primaryConstructor?.call() as T

		inline fun <reified T> fromClass(clazz: T): String = (clazz ?: "")::class.qualifiedName!!
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

	/**
	 * Used to render special effects and/or models on the tool gun's [BlockEntityWithoutLevelRenderer].
	 * Fires before the other render stages.
	 * @see renderScreenStage
	 * @see renderCoilStage
	 * @see renderBodyStage
	 */
	fun render(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render text and/or icons positioned to the tool gun screen, fires after everything else.
	 * @see render
	 */
	fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render effects and/or models to the tool gun's coil, fires after [render] and [renderBodyStage].
	 * This rotates along with the coil.
	 * @see render
	 * @see renderBodyStage
	 */
	fun renderCoilStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	/**
	 * Used to render effects and/or models to the tool gun's main body, fires after [render].
	 * @see render
	 */
	fun renderBodyStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		helper: ToolGunRenderHelper
	)

	fun getModeWidget(): ModeWidget

	fun getDisplayName(): Component

	fun getTooltip(): Component

	fun shouldCoilSpin(stack: ItemStack, displayContext: ItemDisplayContext): Boolean = true

	fun getUid(): ResourceLocation
}