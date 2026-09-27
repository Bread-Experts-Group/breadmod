package org.bread_experts_group.breadmod

import net.fabricmc.api.ModInitializer
import org.bread_experts_group.breadlib.FabricHelper

class BreadmodFabric : ModInitializer {
    override fun onInitialize() {
        Breadmod.LOGGER.info("Hello Mod Template world!")
        Breadmod.init()
        FabricHelper.registerContent(Breadmod.MOD_ID)
    }
}
