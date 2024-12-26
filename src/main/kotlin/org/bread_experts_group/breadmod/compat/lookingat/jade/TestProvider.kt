package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.Capabilities
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.fluid.JadeFluidObject

class TestProvider : IBlockComponentProvider {
	companion object {
		val INSTANCE : TestProvider = TestProvider()
	}

	override fun getUid() : ResourceLocation = JadePlugin.BLOCK_DATA
	override fun appendTooltip(tooltip : ITooltip, accessor : BlockAccessor, config : IPluginConfig) {
		tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
//		val entity = accessor.blockEntity as? BreadModBlockEntity<*> ?: return
		for (direction : Direction in Direction.entries) {
			val handler = accessor.level.getCapability(Capabilities.FluidHandler.BLOCK, accessor.position, direction)
			if (handler != null) {
				tooltip.add(
					FluidBarElement(
						JadeFluidObject.of(
							handler.getFluidInTank(0).fluid,
							handler.getFluidInTank(0).amount.toLong()
						),
						handler.getTankCapacity(0),
						direction
					)
				)
			}
		}
	}
}