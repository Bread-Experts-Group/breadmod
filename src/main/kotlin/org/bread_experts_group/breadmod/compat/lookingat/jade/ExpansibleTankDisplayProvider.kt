package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.config.IPluginConfig

class ExpansibleTankDisplayProvider : IBlockComponentProvider {
	companion object {
		val INSTANCE: ExpansibleTankDisplayProvider = ExpansibleTankDisplayProvider()
	}

	override fun getUid(): ResourceLocation = JadePlugin.BLOCK_DATA
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? FluidBearingBlockEntity ?: return
		tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
		for (direction: Direction in Direction.entries) {
			val handler = accessor.level.getCapability(Capabilities.FluidHandler.BLOCK, accessor.position, direction)
			if (handler != null) {
				tooltip.add(
					FluidBarElement(
						entity.fluidHandler.getTank(0),
						direction
					)
				)
			}
		}
	}
}