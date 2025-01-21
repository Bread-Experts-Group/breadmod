package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FacingSensitiveProviderRetriever
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig

object ExpansibleCellDisplayProvider : IBlockComponentProvider {
	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? EnergyBearingBlockEntity ?: return
		val cell = accessor.blockEntity as? FacingSensitiveProviderRetriever
		tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE)
		if (cell == null) tooltip.add(EnergyBarElement(entity.energyHandler.getCell(0), null))
		else for (direction: Direction in Direction.entries) {
			tooltip.add(
				EnergyBarElement(
					entity.energyHandler.getCell(
						cell.getIndexForBlockProvider(
							Capabilities.EnergyStorage.BLOCK,
							direction
						)
					),
					direction
				)
			)
		}
	}

	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
}