package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu

class FluidEnergyBlock : BreadModBlock(Properties.of()) {
	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true

	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val storage = ExtendedItemHandler.ofSlotsWithCapacity(8, 64)
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(null to { _ -> storage })
		)
	}

	override fun ofMenu(): ((MenuType<*>, Int, Inventory, BreadModBlockEntity) -> BreadModMenu) = ::FluidEnergyMenu
}