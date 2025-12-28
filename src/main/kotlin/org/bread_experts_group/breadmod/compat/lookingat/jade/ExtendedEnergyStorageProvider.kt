package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.compat.lookingat.jade.elements.EnergyElement
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.util.logDebugInfo
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig

object ExtendedEnergyStorageProvider : IBlockComponentProvider {
	@DataGenerateLanguage(name = "Energy Data Provider", prefix = "config.jade.plugin_")
	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? BreadModBlockEntity ?: return
		val energy = entity.getCapabilityOrNull(Capabilities.EnergyStorage.BLOCK) as? ExtendedEnergyHandler ?: return

		logDebugInfo(energy.energyStored)

		tooltip.add(EnergyElement(energy))
	}

	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
}