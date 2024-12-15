package org.bread_experts_group.breadmod.compat.lookingat.jade

import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.experimental.fluid_tank.FluidTankJadeBlock
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class JadePlugin : IWailaPlugin {
    companion object {
        @DataGenerateLanguage("en_us", "Breadmod jade data provider")
        val BLOCK_DATA = modLocation("data_provider")
    }

//    override fun register(registration: IWailaCommonRegistration) {
//        registration.registerBlockDataProvider(TestProvider(), DoughMachineBlockEntity::class.java)
//    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(TestProvider.INSTANCE, FluidTankJadeBlock::class.java)
    }
}