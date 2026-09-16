package org.bread_experts_group.breadmod

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.bread_experts_group.breadlib.ForgeRegistrationHelper

@Mod(Breadmod.MOD_ID)
class BreadmodForge(context: FMLJavaModLoadingContext) {
    init {
        Breadmod.LOGGER.info("Hello Mod Template world!")
        Breadmod.init()
        ForgeRegistrationHelper.registerContent(context.modEventBus, Breadmod.MOD_ID)
    }
}
