package org.bread_experts_group.breadlib_mod_template

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.bread_experts_group.breadlib.NeoForgeRegistrationHelper


@Mod(BreadLibModTemplate.MOD_ID)
class BreadlibModTemplateNeoForge(eventBus: IEventBus) {
    init {
        BreadLibModTemplate.LOGGER.info("Hello Mod Template world!")
        BreadLibModTemplate.init()
        NeoForgeRegistrationHelper.registerContent(eventBus, BreadLibModTemplate.MOD_ID)
    }
}
