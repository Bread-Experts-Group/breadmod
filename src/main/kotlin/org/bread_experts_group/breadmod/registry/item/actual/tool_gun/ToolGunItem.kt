package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent.Key
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.client.render.buffer.render.TestCubeBufferTask
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals.currentModeIndex
import org.bread_experts_group.breadmod.client.render.ToolGunClientGlobals.triggerDelta
import org.bread_experts_group.breadmod.client.render.ToolGunItemRenderer
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import java.util.function.Supplier

class ToolGunItem : Item(
	Properties()
		.stacksTo(1)
		.component(ModDataComponents.TOOL_GUN_DATA, EmptyMode())
		.rarity(Rarity.RARE)
), IRegisterSpecialCreativeTab, IMouseItem, IKeyboardItem {
	class ToolGunItemExtensions : IClientItemExtensions {
		override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
	}

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = getStackInPlayerHand(player)
		if (stack.`is`(ModItems.TOOL_GUN) && !level.isClientSide) {
			val mode = stack.get(ModDataComponents.TOOL_GUN_DATA) ?: return InteractionResultHolder.fail(stack)
			mode.action(level, player, stack)
		} else {
			triggerDelta()
			player.playSound(ModSounds.TOOL_GUN.get(), 0.8f, 1f)
		}
		return super.use(level, player, usedHand)
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

	override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean =
		false

	companion object {
		const val TOOL_GUN_DEF: String = "tool_gun"
	}

	override fun onMouseScroll(scrollingEvent: MouseScrollingEvent, heldStack: ItemStack, player: Player) {
		val mode = heldStack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, EmptyMode())
		if (player.isCrouching) {
			scrollingEvent.isCanceled = true
			val deltaY = scrollingEvent.scrollDeltaY
			val modeSize = toolGunModes.size
			currentModeIndex = Math.floorMod(currentModeIndex + deltaY.toInt(), modeSize)
			PacketDistributor.sendToServer(ToolGunModeChangePacket(toolGunModes.keys.elementAt(currentModeIndex)))
		}
		if (mode.mouseScrollAction(scrollingEvent, heldStack, player)) scrollingEvent.isCanceled = true
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		val mode = heldStack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, EmptyMode())
		mode.mouseButtonPostAction(mouseEvent, heldStack, player)
	}

	override fun onMouseInputPre(mouseEvent: Pre, heldStack: ItemStack, player: Player) {
		val mode = heldStack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, EmptyMode())
		mode.mouseButtonPreAction(mouseEvent, heldStack, player)
	}

	// todo figure out key modifiers in the if statement
	override fun onKeyboardPress(keyEvent: Key, heldStack: ItemStack, player: Player) {
		val mode = heldStack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, EmptyMode())
		if (keyEvent.key == openModeGui.key.value && localClient.screen == null) {
			localClient.setScreen(ToolGunScreen(Component.literal("Tool Gun: Mode Select")))
		}
		mode.keyboardInputAction(keyEvent, heldStack, player)
		if (keyEvent.key == InputConstants.KEY_PERIOD && keyEvent.action == InputConstants.PRESS) {
			TestCubeBufferTask.create(player.position())
		}
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(Component.literal("Press PERIOD to spawn a bread block!"))
	}
}