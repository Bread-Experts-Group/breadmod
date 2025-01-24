package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.util.function.Supplier

internal object PhysXTestTool : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level is ServerLevel) return InteractionResultHolder.pass(player.getItemInHand(usedHand))
		player.sendSystemMessage(Component.literal("Not available"))
		return super.use(level, player, usedHand)
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.EXPERIMENTAL_TAB)
}