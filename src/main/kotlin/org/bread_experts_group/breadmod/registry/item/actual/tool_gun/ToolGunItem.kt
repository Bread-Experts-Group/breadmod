package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.bread_experts_group.breadmod.client.render.item.ToolGunItemRenderer
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.util.function.Supplier

// todo complete re-implementation of tool gun features
class ToolGunItem : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
	class ToolGunItemExtensions : IClientItemExtensions {
		override fun getCustomRenderer() : BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
	}

	override fun use(level : Level, player : Player, usedHand : InteractionHand) : InteractionResultHolder<ItemStack> {
		if (usedHand != InteractionHand.MAIN_HAND) return InteractionResultHolder.fail(player.getItemInHand(usedHand))
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide)
	}

	override val creativeModeTabs : List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

	companion object {
		const val TOOL_GUN_DEF : String = "tool_gun"
	}
}