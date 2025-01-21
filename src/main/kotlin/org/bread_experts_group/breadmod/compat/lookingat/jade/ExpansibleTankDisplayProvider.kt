package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.FacingSensitiveProviderRetriever
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig

object ExpansibleTankDisplayProvider : IBlockComponentProvider {
	override fun getUid(): ResourceLocation = modLocation("fluid_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? FluidBearingBlockEntity ?: return
		val tank = accessor.blockEntity as? FacingSensitiveProviderRetriever
		tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
		if (tank == null) tooltip.add(FluidBarElement(entity.fluidHandler.getTank(0), null))
		else for (direction: Direction in Direction.entries) {
			tooltip.add(
				FluidBarElement(
					entity.fluidHandler.getTank(
						tank.getIndexForBlockProvider(
							Capabilities.FluidHandler.BLOCK,
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