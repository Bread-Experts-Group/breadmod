package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FacingSensitiveProviderRetriever
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig

object ExpansibleCellDisplayProvider : IBlockComponentProvider {
	@DataGenerateLanguage("en_us", "Energy Data Provider", prefix = "config.jade.plugin_")
	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? EnergyBearingBlockEntity ?: return
		val sides = accessor.blockEntity as? FacingSensitiveProviderRetriever
		tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE)
		if (sides == null) {
			for (cellIndex in 0 ..< entity.energyHandler.getUnits()) {
				tooltip.add(EnergyBarElement(entity.energyHandler.getUnit(cellIndex), null))
			}
		} else {
			for (direction: Direction in Direction.entries) {
				val cellIndex = sides.getIndexForBlockProvider(Capabilities.EnergyStorage.BLOCK, direction)
				if (cellIndex > 0) tooltip.add(EnergyBarElement(entity.energyHandler.getUnit(cellIndex), direction))
			}
		}
	}

	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
}