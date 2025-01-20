package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.config.IPluginConfig

object ExpansibleCellDisplayProvider : IBlockComponentProvider {
	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? EnergyBearingBlockEntity ?: return
		tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE)
		for (direction: Direction in Direction.entries) {
			tooltip.add(
				EnergyBarElement(
					entity.energyHandler.getCell(0),
					direction
				)
			)
		}
	}
}