package org.bread_experts_group.breadmod.registry.item.actual.armor

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.data_holders.common.MachSpeedData
import org.bread_experts_group.breadmod.util.itemTooltip

/**
 * Chef Hat, inspired from the game Pizza Tower by Tour De Pizza.
 *
 * @author Logan Mclean
 * @since 1.0.0
 * @see ChefHatArmorLayer
 * @see ChefHatModel
 */
class ChefHatItem : ArmorItem(ModArmorMaterials.CHEF, Type.HELMET, Properties().stacksTo(1)) {
	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(this.itemTooltip().withStyle(ChatFormatting.LIGHT_PURPLE))
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (slotId != 39) return
		val player = entity as? Player ?: return
		val data = MachSpeedData.get(stack)
		if (entity.isSprinting) {
			data.tick(player, level, slotId)
		} else data.reset(entity)
		stack.set(ModDataComponents.MACH_SPEED, data)
	}
}