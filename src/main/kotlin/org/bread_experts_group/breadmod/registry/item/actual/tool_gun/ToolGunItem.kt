package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent.Key
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.bread_experts_group.breadmod.client.render.buffer.render.TestCubeBufferTask
import org.bread_experts_group.breadmod.client.render.item.ToolGunItemRenderer
import org.bread_experts_group.breadmod.client.tool_gun_mode.TestScreen
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunModeData
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunAnimationHandler
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import java.util.function.Supplier

// todo complete re-implementation of tool gun features
class ToolGunItem : Item(
	Properties()
		.stacksTo(1)
		.component(ModDataComponents.TOOL_GUN_DATA, ToolGunModeData.EMPTY)
), IRegisterSpecialCreativeTab, IMouseItem, IKeyboardItem {
	class ToolGunItemExtensions : IClientItemExtensions {
		override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
	}

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(usedHand)
		if (stack.`is`(ModItems.TOOL_GUN) && !level.isClientSide) {
			val data = stack.get(ModDataComponents.TOOL_GUN_DATA) ?: return InteractionResultHolder.fail(stack)
			data.actionClass.action(level, player, stack)
		} else {
			ToolGunAnimationHandler.trigger()
		}
		return super.use(level, player, usedHand)
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

	companion object {
		const val TOOL_GUN_DEF: String = "tool_gun"
	}

	override fun onMouseScroll(scrollingEvent: MouseScrollingEvent, heldStack: ItemStack, player: Player) {
		if (player.isShiftKeyDown) {
			player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 1f)
			player.sendSystemMessage(Component.literal("Scrolling Cancelled"))
			scrollingEvent.isCanceled = true
		}
	}

	override fun onMouseInput(mouseEvent: Post, heldStack: ItemStack, player: Player) {
//		if (mouseEvent.button == InputConstants.MOUSE_BUTTON_RIGHT && mouseEvent.action == InputConstants.PRESS) {
//			PacketDistributor.sendToServer(ToolGunActionPacket())
//		}
	}

	override fun onKeyboardPress(keyEvent: Key, heldStack: ItemStack, player: Player) {
		if (keyEvent.key == openModeGui.key.value && localClient.screen == null) {
			localClient.setScreen(TestScreen(Component.literal("Tool Gun: Mode Select")))
		}
		if (keyEvent.key == InputConstants.KEY_PERIOD && keyEvent.action == InputConstants.PRESS) {
			TestCubeBufferTask.create(player.position())
		}
		ToolGunModeDataLoader.modes.forEach { (_, u) ->
			u.forEach { (_, data) ->
				if (keyEvent.key == data.keyMapping.key) println("hooray")
			}
		}
	}
}