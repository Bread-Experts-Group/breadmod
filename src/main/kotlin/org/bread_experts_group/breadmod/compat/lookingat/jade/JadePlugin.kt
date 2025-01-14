package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlock
import org.bread_experts_group.breadmod.experimental.fluid_tank.FluidTankJadeBlock
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid.SingleFluidRecipeBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.DoughMachineBlock
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

// MekanismJadePlugin.java !!!
// todo items, proper fluid, energy provider
@WailaPlugin
class JadePlugin : IWailaPlugin {
	companion object {
		val BLOCK_DATA: ResourceLocation = modLocation("data_provider")
	}

	override fun registerClient(registration: IWailaClientRegistration) {
		registration.registerBlockComponent(TestProvider.INSTANCE, FluidTankJadeBlock::class.java)
		registration.registerBlockComponent(TestProvider.INSTANCE, SingleFluidRecipeBlock::class.java)
		registration.registerBlockComponent(TestProvider.INSTANCE, FluidEnergyBlock::class.java)
		registration.registerBlockComponent(TestProvider.INSTANCE, DoughMachineBlock::class.java)
	}
}