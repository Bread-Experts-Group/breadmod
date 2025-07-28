package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.client.render.texture.GuiElement
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements

class LambdaSlotItemHandler(
	handler: IItemHandler,
	index: Int,
	x: Int, y: Int,
	private val lMayPickup: (Player) -> Boolean = { true },
	private val lMayPlace: (ItemStack) -> Boolean = { true },
	val jadeGraphic: GuiElement = ModGuiElements.SLOT
) : SlotItemHandler(handler, index, x, y) {
	override fun mayPickup(playerIn: Player): Boolean = this.lMayPickup(playerIn)
	override fun mayPlace(stack: ItemStack): Boolean = this.lMayPlace(stack)

	companion object {
		fun playerReadOnly(
			handler: IItemHandler,
			index: Int,
			x: Int, y: Int
		): LambdaSlotItemHandler = LambdaSlotItemHandler(
			handler, index, x, y,
			lMayPlace = { false },
			jadeGraphic = ModGuiElements.RESULT_SLOT
		)

		fun fluidHandlerOnly(
			handler: IItemHandler,
			index: Int,
			x: Int, y: Int,
			restrict: (FluidStack) -> Boolean = { true }
		): LambdaSlotItemHandler = LambdaSlotItemHandler(
			handler, index, x, y,
			lMayPlace = {
				val contained = FluidUtil.getFluidContained(it)
				if (contained.isEmpty) return@LambdaSlotItemHandler false
				restrict(contained.get())
			},
			jadeGraphic = ModGuiElements.BUCKET_SLOT
		)
	}
}