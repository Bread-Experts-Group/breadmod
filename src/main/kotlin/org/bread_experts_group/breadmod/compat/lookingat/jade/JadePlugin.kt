package org.bread_experts_group.breadmod.compat.lookingat.jade

import org.bread_experts_group.breadmod.experimental.fluid_tank.FluidTankJadeBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlock
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

// MekanismJadePlugin.java !!!
// todo items, proper fluid, energy provider
@WailaPlugin
@Suppress("unused")
class JadePlugin : IWailaPlugin {
	override fun registerClient(registration: IWailaClientRegistration) {
		registration.registerBlockComponent(ExpansibleTankDisplayProvider, FluidTankJadeBlock::class.java)
		registration.registerBlockComponent(ExpansibleTankDisplayProvider, FluidEnergyBlock::class.java)
		registration.registerBlockComponent(ExpansibleTankDisplayProvider, DoughMachineBlock::class.java)

		registration.registerBlockComponent(ExpansibleCellDisplayProvider, DoughMachineBlock::class.java)
		registration.registerBlockComponent(ExpansibleCellDisplayProvider, FluidEnergyBlock::class.java)
	}
}