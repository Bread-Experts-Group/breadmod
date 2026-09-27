package org.bread_experts_group.breadmod

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.bread_experts_group.breadlib.NeoForgeHelper
import org.bread_experts_group.breadmod.Breadmod.Companion.MOD_ID


@Mod(MOD_ID)
class BreadmodNeoForge(eventBus: IEventBus) {
    init {
        Breadmod.LOGGER.info("Hello Mod Template world!")
        Breadmod.init()
        NeoForgeHelper.registerContent(eventBus, MOD_ID)
        NeoForgeHelper.runDataGenerator(eventBus, MOD_ID)
    }
}
