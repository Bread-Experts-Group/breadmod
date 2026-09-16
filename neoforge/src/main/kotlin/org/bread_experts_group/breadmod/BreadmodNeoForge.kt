package org.bread_experts_group.breadmod

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.bread_experts_group.breadlib.NeoForgeRegistrationHelper


@Mod(Breadmod.MOD_ID)
class BreadmodNeoForge(eventBus: IEventBus) {
    init {
        Breadmod.LOGGER.info("Hello Mod Template world!")
        Breadmod.init()
        NeoForgeRegistrationHelper.registerContent(eventBus, Breadmod.MOD_ID)
    }
}
