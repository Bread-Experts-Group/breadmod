package org.bread_experts_group.breadlib_mod_template

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.bread_experts_group.breadlib.ForgeRegistrationHelper

@Mod(BreadLibModTemplate.MOD_ID)
class BreadLibModTemplateForge(context: FMLJavaModLoadingContext) {
    init {
        BreadLibModTemplate.LOGGER.info("Hello Mod Template world!")
        BreadLibModTemplate.init()
        ForgeRegistrationHelper.registerContent(context.modEventBus, BreadLibModTemplate.MOD_ID)
    }
}
