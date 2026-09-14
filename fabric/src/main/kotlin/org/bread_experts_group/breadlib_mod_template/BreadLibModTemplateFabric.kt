package org.bread_experts_group.breadlib_mod_template

import net.fabricmc.api.ModInitializer
import org.bread_experts_group.breadlib.FabricRegistrationHelper

class BreadLibModTemplateFabric : ModInitializer {
    override fun onInitialize() {
        BreadLibModTemplate.LOGGER.info("Hello Mod Template world!")
        BreadLibModTemplate.init()
        FabricRegistrationHelper.registerContent(BreadLibModTemplate.MOD_ID)
    }
}
