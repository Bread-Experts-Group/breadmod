package org.bread_experts_group.breadmod.tool_gun

import net.minecraft.ChatFormatting
import net.minecraft.client.model.HumanoidModel.ArmPose
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.client.render.buffer.BeamBufferTask
import org.bread_experts_group.breadmod.client.render.item.ToolGunItemRenderer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import java.util.function.Supplier

class ToolGunItem : Item(
	Properties()
		.stacksTo(1)
		.component(ModDataComponents.TOOL_GUN_DATA, ToolGunData.EMPTY)
		.rarity(Rarity.RARE)
), IRegisterSpecialCreativeTab, IMouseItem, IKeyboardItem {
	private val logger: Logger = LogManager.getLogger("Tool Gun")

	object ToolGunItemExtensions : IClientItemExtensions {
		private var renderer: String = "tool_gun_renderer"
		override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer =
			Registry.itemRenderers.getOrPut(this.renderer, ::ToolGunItemRenderer)

		override fun getArmPose(entityLiving: LivingEntity, hand: InteractionHand, itemStack: ItemStack): ArmPose =
			ArmPose.BOW_AND_ARROW
	}

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = getStackInPlayerHand(player, usedHand)
		if (stack.`is`(ModItems.TOOL_GUN)) {
			val mode = ToolGunData.get(stack).getMode()
			if (mode.actionPre(level, player, usedHand)) {
				mode.action(level, player, stack)
				mode.actionPost(level, player, usedHand)

				if (level.isClientSide) {
					(IClientItemExtensions.of(stack).customRenderer as ToolGunItemRenderer).triggerDelta()
					if (mode.getCustomRenderer().shouldFireBeam()) BeamBufferTask.needsNewTask = true
					if (mode.shouldPlayToolGunSound(stack, player)) mode.playToolGunSound(player)
				}
			}
		}
		return super.use(level, player, usedHand)
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

	companion object {
		const val TOOL_GUN_DEF: String = "tool_gun"

		@DataGenerateLanguage(name = "Tool Gun")
		val TOOL_GUN_SCREEN_NAME: Component = Component.translatable(this.TOOL_GUN_DEF, "screen_name")
	}

	override fun onMouseScroll(scrollingEvent: MouseScrollingEvent, heldStack: ItemStack, player: Player) {
		val data = ToolGunData.get(heldStack)
		if (player.isCrouching) {
			scrollingEvent.isCanceled = true
			val currentIndex =
				Math.floorMod(data.modeIndex + scrollingEvent.scrollDeltaY.toInt(), Registry.toolGunModes.size)
			PacketDistributor.sendToServer(
				ToolGunModeChangePacket(
					Registry.toolGunModes.keys.elementAt(currentIndex),
					currentIndex
				)
			)
		}
		if (data.getMode().mouseScrollAction(scrollingEvent, heldStack, player)) scrollingEvent.isCanceled = true
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		val newData = ToolGunData.get(stack)
		if (newData.extraData.isEmpty) {
			this.logger.info("Tool gun data is empty! Populating mode saved data...")
			Registry.toolGunModes.forEach { (_, mode) ->
				newData.extraData.put(mode.getModeName(), CompoundTag().also { mode.saveExtraData(it, level) })
			}
			stack.set(ModDataComponents.TOOL_GUN_DATA, newData)
		}
		if (!newData.dataLoaded) newData.loadData(level)
	}

	override fun onMouseInputPre(mouseEvent: Pre, heldStack: ItemStack, player: Player) {
		val mode = ToolGunData.get(heldStack).getMode()
		mode.mouseButtonPreAction(mouseEvent, heldStack, player)
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		val mode = ToolGunData.get(heldStack).getMode()
		mode.mouseButtonPostAction(mouseEvent, heldStack, player)
	}

	override fun onKeyboardPress(keyEvent: Key, heldStack: ItemStack, player: Player) {
		val data = ToolGunData.get(heldStack)
		if (data.getMode().keyMatchesInput(openModeGui, keyEvent) && localClient.screen == null) {
			localClient.setScreen(ToolGunScreen(Companion.TOOL_GUN_SCREEN_NAME, heldStack))
		} else {
			(data.keyData[keyEvent.key] ?: return).second.invoke(keyEvent, heldStack, player, data)
		}
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val mode = ToolGunData.get(stack).getMode()
		tooltipComponents.addAll(
			listOf(
				Component.literal("Current Mode: ")
					.append(
						Component.translatable(
							"%s", mode.getModeName().replace('_', ' ').replaceFirstChar(Char::uppercaseChar)
						).withStyle(ChatFormatting.GOLD)
					),
				Component.literal("Press PERIOD to spawn a bread block!")
			)
		)
	}
}