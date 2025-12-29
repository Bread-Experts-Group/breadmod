package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.compat.lookingat.jade.elements.EnergyElement
import org.bread_experts_group.breadmod.compat.lookingat.jade.elements.FluidElement
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.util.getBigDecimal
import org.bread_experts_group.breadmod.util.getFluid
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig
import java.math.BigDecimal

object ExtendedHandlerComponentProvider : IBlockComponentProvider {
	@DataGenerateLanguage(name = "BM Handler Block Component Provider", prefix = "config.jade.plugin_")
	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
	override fun getDefaultPriority(): Int = TooltipPosition.TAIL

	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		this.getEnergyStats(accessor)?.let { (stored, capacity) ->
			tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE)
			tooltip.add(EnergyElement(stored, capacity))
		}

		this.getFluidStats(accessor)?.forEach { (fluid, stored, capacity) ->
			tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
			tooltip.add(FluidElement(fluid, stored, capacity, accessor.side))
		}
	}

	private fun getEnergyStats(accessor: BlockAccessor): Pair<BigDecimal, BigDecimal>? {
		val data = accessor.serverData
		return if (data.contains("bm_energy_stored") && data.contains("bm_energy_capacity")) {
			accessor.serverData.getBigDecimal("bm_energy_stored") to
					accessor.serverData.getBigDecimal("bm_energy_capacity")
		} else null
	}

	private fun getFluidStats(accessor: BlockAccessor): List<Triple<Fluid, BigDecimal, BigDecimal>>? {
		val data = accessor.serverData
		if (!data.contains("bm_fluid_tanks")) return null
		val tag = data.getList("bm_fluid_tanks", 9)

		return buildList {
			repeat(tag.size) { i ->
				val compound = tag.getCompound(i)
				this.add(
					Triple(
						compound.getFluid("fluid"),
						compound.getBigDecimal("stored"),
						compound.getBigDecimal("capacity")
					)
				)
			}
		}
	}

	private fun getItemStats(accessor: BlockAccessor): Nothing = TODO()
}