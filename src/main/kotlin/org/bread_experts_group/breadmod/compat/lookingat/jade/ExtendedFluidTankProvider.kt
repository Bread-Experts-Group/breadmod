package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.compat.lookingat.jade.elements.FluidElement
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig

object ExtendedFluidTankProvider : IBlockComponentProvider {
	@DataGenerateLanguage(name = "Fluid Data Provider", prefix = "config.jade.plugin_")
	override fun getUid(): ResourceLocation = modLocation("fluid_data_provider")
	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val entity = accessor.blockEntity as? BreadModBlockEntity ?: return
		val fluidHandler =
			entity.getCapabilityOrNull(Capabilities.FluidHandler.BLOCK) as? ExtendedFluidHandler ?: return
		tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
/*		if (sides == null) {
			for (tankIndex in 0 ..< entity.fluidHandler.getUnits()) {
				tooltip.add(FluidBarElement(entity.fluidHandler.getUnit(tankIndex), null))
			}
		} else {
			for (direction: Direction in Direction.entries) {
				val tankIndex = direction.getIndexForBlockProvider(Capabilities.FluidHandler.BLOCK, direction)
				if (tankIndex > 0) tooltip.add(FluidBarElement(entity.fluidHandler.getUnit(tankIndex), direction))
			}
		}*/

		fluidHandler.tanks.forEach { (_, tank) ->
			tooltip.add(FluidElement(tank, accessor.side))
		}
	}

	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
}