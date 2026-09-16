package org.bread_experts_group.breadmod

import net.fabricmc.api.ModInitializer
import org.bread_experts_group.breadlib.FabricRegistrationHelper

class BreadmodFabric : ModInitializer {
    override fun onInitialize() {
        Breadmod.LOGGER.info("Hello Mod Template world!")
        Breadmod.init()
        FabricRegistrationHelper.registerContent(Breadmod.MOD_ID)
    }
}
