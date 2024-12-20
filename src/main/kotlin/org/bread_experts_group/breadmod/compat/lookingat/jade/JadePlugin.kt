package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyBlock
import org.bread_experts_group.breadmod.experimental.fluid_tank.FluidTankJadeBlock
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid.SingleFluidRecipeBlock
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class JadePlugin : IWailaPlugin {
	companion object {
		@DataGenerateLanguage("en_us", "Breadmod jade data provider")
		val BLOCK_DATA : ResourceLocation = modLocation("data_provider")
	}

	override fun registerClient(registration : IWailaClientRegistration) {
		registration.registerBlockComponent(TestProvider.INSTANCE, FluidTankJadeBlock::class.java)
		registration.registerBlockComponent(TestProvider.INSTANCE, SingleFluidRecipeBlock::class.java)
		registration.registerBlockComponent(TestProvider.INSTANCE, FluidEnergyBlock::class.java)
	}
}