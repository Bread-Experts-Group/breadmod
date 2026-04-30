package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.registry.block.actual.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.WheatCrusherBlock
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlock
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import kotlin.reflect.KClass

//@WailaPlugin
@Suppress("unused")
class JadePlugin : IWailaPlugin {
	override fun registerClient(registration: IWailaClientRegistration) {
		this.registerBMComponent(registration, DoughMachineBlock::class.java)
		this.registerBMComponent(registration, WheatCrusherBlock::class.java)
		this.registerBMComponent(registration, FluidEnergyBlock::class.java)
	}

	override fun register(registration: IWailaCommonRegistration) {
		this.registerBMBlocks(
			registration,
			DoughMachineBlock::class,
			WheatCrusherBlock::class,
			FluidEnergyBlock::class
		)
	}

	private fun registerBMBlocks(registration: IWailaCommonRegistration, vararg blocks: KClass<*>) {
		blocks.forEach { registration.registerBlockDataProvider(ExtendedHandlerDataProvider, it.java) }
	}

	private fun <T : Block> registerBMComponent(registration: IWailaClientRegistration, block: Class<T>) {
		registration.registerBlockComponent(ExtendedHandlerComponentProvider, block)
	}
}